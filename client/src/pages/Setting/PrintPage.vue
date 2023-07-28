<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-12">
      <div class="row">
        <div class="col-12 tw-px-2">
          <div class="bg-primary text-white rounded-borders tw-mb-4">
            <q-toolbar>
              <q-toolbar-title shrink>{{ $t('field.print_template') }}</q-toolbar-title>
              <q-tabs v-model="activeGroupId" shrink stretch class="tw-ml-4">
                <q-tab v-for="group in groups" :name="group.name" :label="group.label"/>
              </q-tabs>
            </q-toolbar>
            <q-toolbar>
              <IconMage src="/images/print.png" color="white"/>
              <Select v-model="activeDoc.id"
                      :placeholder="$t('field.template')" :options="docs"
                      emit-value color="primary" bg-color="brand" dense class="tw-w-1/4 tw-ml-3"/>
              <Button :label="$t('field.add')" icon="fa-solid fa-plus"
                      stretch flat color="white" no-caps @click="add(null, true)"/>
              <Button :label="$t('field.save')" icon="fa-solid fa-floppy-disk"
                      stretch flat color="white" class="tw-ml-2" no-caps/>
              <Button :label="$t('field.reset')" icon="fa-solid fa-arrows-rotate"
                      stretch flat color="white" class="tw-ml-2" no-caps/>
              <Button :label="$t('field.delete')" icon="fa-solid fa-trash"
                      stretch flat color="white" class="tw-ml-2" no-caps/>
              <q-space/>
              <Button :label="$t('field.help')" icon="fa-solid fa-circle-question"
                      stretch flat color="white" class="tw-ml-2" no-caps/>
            </q-toolbar>
          </div>
          <div class="tw-flex">

          </div>
          <TextField v-model="activeDoc.name" :rules="docNamingRules"
                     maxlength="20" icon="fa-solid fa-scroll" class="tw-w-1/4 tw-mt-4">
            <template #after>
              <q-chip v-if="activeDoc.localOnly" color="positive" text-color="white" icon="fa-solid fa-star" :label="$t('field.new')"/>
            </template>
          </TextField>
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-md-6 tw-px-2">
          <RichTextEditor v-model="activeDoc.content" height="600px" :loading="loading" :tags="tags"/>
        </div>
        <div class="col-12 col-md-6 tw-px-2 tw-pt-[88px]">
          <div class="print-preview tw-min-h-[512px] tw-p-1" v-html="activeDoc.content"></div>
        </div>
      </div>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import RichTextEditor from "components/General/Other/RichTextEditor.vue";
import Button from "components/General/Other/Button.vue";

import {onMounted, ref, reactive, watch, computed} from "vue";
import {axios, error, util} from "src/boot"
import {useI18n} from "vue-i18n";
import Select from "components/General/Other/Select.vue";
import IconMage from "components/General/Other/IconMage.vue";
import TextField from "components/General/Other/TextField.vue";

export default {
  name: 'PrintPage',

  components: {TextField, IconMage, Select, Button, RichTextEditor, Page},

  setup() {
    // Get $t
    const $t = useI18n().t

    // ****************************
    // State
    // ****************************
    const loading = ref(true)

    // ****************************
    // Editor's data
    // ****************************
    // Editor's tags
    const tags = ref([])
    // Editor's content
    const activeDoc = reactive({
      id: null,
      name: '',
      content: '',
      ref: null
    })
    // Documents
    const docs = ref([])
    const docNamingRules = [
      v => !!v || $t('message.required'),
      v => v.length <= 20 || $t('message.max_length', {max: 20})
    ]
    /**
     * Load docs
     *
     * @param res
     */
    const loadDocs = (res) => {
      // Load doc list
      res.data.payload.documents.map(add)
      // Load active doc
      loadActiveDoc(res.data.payload.documents[0])
    }
    /**
     * Load active doc
     *
     * @param res
     */
    const loadActiveDoc = (res) => {
      // Get payload
      const payload = util.isUnset(res.data) ? res : res.data.payload
      // Load active doc
      activeDoc.id = `${payload.id}`
      activeDoc.name = payload.name
      activeDoc.localOnly = payload.localOnly
      activeDoc.content = !!payload.localOnly ? payload.localContent : payload.content
      activeDoc.ref = !!payload.localOnly ? res : null
    }
    /**
     * Add new doc locally
     */
    const add = (item = null, autoActive = false) => {
      // Check if item is null
      const isNull = item === null
      // Create new Doc
      const newDoc = {
        id: isNull ? `-${docs.value.length}` : `${item.id}`,
        name: isNull ? `${$t('field.template')} ${docs.value.length + 1}` : item.name,
        localContent: '',
        localOnly: isNull
      }
      // Add new Doc to Doc list
      docs.value.push(newDoc)
      // Set new Doc active if auto active
      autoActive && loadActiveDoc(newDoc)
    }
    // Watch if activeDoc.id is changed, fetch new Document
    watch(() => activeDoc.id, util.skipNull((value) => {
      // Check if activeDoc.id is start with '-'
      if (value.startsWith('-')) {
        // Set active
        loadActiveDoc(docs.value.find(doc => doc.id === value))
      } else {
        // Fetch Document
        axios.get(`print/${value}`).then(loadActiveDoc)
      }
    }))
    // Watch if activeDoc.content is changed, apply changes to localContent
    watch(() => activeDoc.content, (value) => {
      // Only do this for locally added doc
      if (!util.isUnset(activeDoc.ref)) {
        // Set content
        activeDoc.ref.localContent = value
      }
    })

    // ****************************
    // General info
    // ****************************
    const groups = ref([])
    const activeGroupId = ref(null)
    // Watch if activeGroupId is changed, fetch new Documents
    watch(activeGroupId, util.skipNull((value) => {
      // Set activeDoc id to null
      activeDoc.id = null
      // Fetch Documents
      axios.get(`print/group/${value}`).then(loadDocs)
    }))

    // Index fetch
    onMounted(() => axios.get('print/index')
      .then(res => {
        // Load docs
        loadDocs(res)
        // Apply tags
        tags.value = res.data.payload.tags

        // Apply general info
        groups.value = res.data.payload.groups.map(group => ({
          name: `${group.id}`,
          label: $t(`field.${group.key}`)
        }))
        activeGroupId.value = `${res.data.payload.groups[0].id}`
      })
      .catch(error.any)
      .finally(() => loading.value = false))

    return {
      // State
      loading: loading,
      // Document
      tags: tags, activeDoc: activeDoc, docs: docs, docNamingRules: docNamingRules,
      add: add,
      // General
      groups: groups, activeGroupId: activeGroupId
    }
  },

  computed: {
    // Breadcrumbs
    breadcrumbs() {
      return [
        {label: this.$t('field.setting'), to: '/setting', icon: 'fa-gear'},
        {label: this.$t('field.print'), to: '/setting/print', icon: 'fa-print'},
      ]
    }
  },

  methods: {
    print() {
      console.warn(this.content)
    }
  }
}
</script>
