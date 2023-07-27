<template>
  <Page :breadcrumbs="breadcrumbs">
    <div class="col-12">
      <div class="row">
        <Button label="ahihi" flat @click="print"/>
      </div>
      <div class="row">
        <div class="col-12 col-md-6 tw-px-2">
          <RichTextEditor v-model="doc.content" height="600px" :loading="loading" :tags="tags"/>
        </div>
        <div class="col-12 col-md-6 tw-px-2 tw-pt-[88px]">
          <div class="print-preview tw-min-h-[512px] tw-p-1" v-html="doc.content"></div>
        </div>
      </div>
    </div>
  </Page>
</template>

<script>
import Page from "components/General/Layout/Page.vue";
import RichTextEditor from "components/General/Other/RichTextEditor.vue";
import Button from "components/General/Other/Button.vue";

import {onMounted, ref, reactive} from "vue";
import {axios, error} from "src/boot"

export default {
  name: 'PrintPage',

  components: {Button, RichTextEditor, Page},

  setup() {
    // Ready state
    const loading = ref(true)
    // Editor's print tags
    const tags = ref([])
    // Editor's content
    const doc = reactive({
      id: -1,
      content: ''
    })
    // Fetch print tags
    onMounted(() => axios.get('print/index')
      .then(res => {
        // Apply document
        doc.id = res.data.payload.document.id
        doc.content = res.data.payload.document.content
        // Apply tags
        tags.value = res.data.payload.tags
      })
      .catch(error.any)
      .finally(() => loading.value = false))

    return {
      loading: loading,
      tags: tags,
      doc: doc,
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
