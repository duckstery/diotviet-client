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
            <q-toolbar inset>
              <Select v-model="activeDoc.id"
                      :placeholder="$t('field.template')" :options="docs"
                      emit-value color="primary" bg-color="brand" dense class="tw-w-1/4"/>
              <Button :label="$t('field.add')" icon="fa-solid fa-plus"
                      stretch flat color="white" no-caps/>
              <Button :label="$t('field.collapse')" icon="fa-solid fa-down-left-and-up-right-to-center"
                      stretch flat color="white" class="tw-ml-2" no-caps/>
              <Button :label="$t('field.add')" icon="fa-solid fa-plus"
                      stretch flat color="white" class="tw-ml-2" no-caps/>
              <Button :label="$t('field.collapse')" icon="fa-solid fa-down-left-and-up-right-to-center"
                      stretch flat color="white" class="tw-ml-2" no-caps/>
            </q-toolbar>
          </div>
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

import {onMounted, ref, reactive, watch} from "vue";
import {axios, error, util} from "src/boot"
import {useI18n} from "vue-i18n";
import Select from "components/General/Other/Select.vue";

export default {
  name: 'PrintPage',

  components: {Select, Button, RichTextEditor, Page},

  setup() {
    // Get $t
    const $t = useI18n().t

    /** Ready state */
    const loading = ref(true)

    /** Editor's data */
    // Editor's tags
    const tags = ref([])
    // Editor's content
    const activeDoc = reactive({
      id: '',
      content: ''
    })
    // Documents
    const docs = ref([])
    const loadDocs = (rawDocs) => docs.value = rawDocs.map(doc => ({label: doc.name, value: `${doc.id}`}))

    /** General info */
    const groups = ref([])
    const activeGroupId = ref(null)

    // Watch if activeGroupId is changed, fetch new Document
    watch(activeGroupId, util.skipNull((value) => {
      axios.get(`print/group/${value}`)
        .then(res => {
          // Apply list of group's Document
          docs.value = loadDocs(res.data.payload.documents)
          // Apply active Document
          activeDoc.content = res.data.payload.documents[0].content
        })
    }))

    // Index fetch
    onMounted(() => axios.get('print/index')
      .then(res => {
        // Apply list of group's Document
        docs.value = loadDocs(res.data.payload.documents)
        // Apply active Document
        activeDoc.id = `${res.data.payload.documents[0].id}`
        // activeDoc.content = res.data.payload.documents[0].content
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
      loading: loading,
      tags: tags, activeDoc: activeDoc, docs: docs,
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
