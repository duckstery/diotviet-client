<template>
  <div class="rich-text-editor">
    <Skeleton :model-value="!loading && isReady" :height="height">
      <Editor v-model="model" tinymce-script-src="/tinymce/tinymce.min.js" :init="init"/>
    </Skeleton>
  </div>
</template>

<script>
import Editor from "@tinymce/tinymce-vue"
import {mapState} from "pinia";
import {useEnvStore} from "stores/env";
import Skeleton from "components/General/Other/Skeleton.vue";

export default {
  name: "RichTextEditor",

  components: {
    Skeleton,
    Editor
  },

  props: {
    // Model
    modelValue: String,
    // Readonly mode
    readonly: Boolean,
    // Padding top with toolbar height
    padding: Boolean,
    // Sticky toolbar
    stickyToolbar: Boolean,
    // Tags
    tags: Array,
    // Loading state
    loading: Boolean,
    // Editor height
    height: {
      type: String,
      default: '500px'
    }
  },

  emits: ['update:model-value'],

  data: () => ({
    instance: null,
    isReady: true,
  }),

  computed: {
    // V-model
    model: {
      get() {
        return this.modelValue
      },
      set(value) {
        this.$emit('update:model-value', value)
      }
    },
    // Init configuration
    init() {
      return {
        plugins: 'preview importcss searchreplace autolink directionality code visualblocks visualchars fullscreen image link media codesample table charmap pagebreak nonbreaking anchor insertdatetime advlist lists wordcount help charmap quickbars emoticons',
        editimage_cors_hosts: ['picsum.photos'],
        menubar: 'file edit view insert format tools table help',
        toolbar: 'undo redo | bold italic underline strikethrough | fontfamily fontsize blocks | alignleft aligncenter alignright alignjustify | outdent indent |  numlist bullist | forecolor backcolor removeformat | pagebreak | charmap emoticons | fullscreen  preview print | insertfile image media template link anchor code codesample | ltr rtl | componenttags',
        toolbar_sticky: this.stickyToolbar,
        image_advtab: true,
        importcss_append: true,
        height: this.height,
        image_caption: true,
        quickbars_selection_toolbar: 'bold italic | quicklink h2 h3 blockquote quickimage quicktable',
        noneditable_class: 'mceNonEditable',
        toolbar_mode: 'sliding',
        contextmenu: 'link image table',
        skin: this.$q.dark.isActive ? 'oxide-dark' : 'oxide',
        content_css: [this.$q.dark.isActive ? 'dark' : 'default', '/src/css/tinymce_iframe.scss'],
        promotion: false,
        elementpath: false,
        language: this.language,
        setup: this.setup
      }
    },

    // Map 'env' store
    ...mapState(useEnvStore, ['language'])
  },

  watch: {
    init() {
      this.forceReload()
    },
    tags() {
      this.forceReload()
    }
  },

  methods: {
    /**
     * Setup Editor
     *
     * @param editor
     */
    setup(editor) {
      /* example, adding a toolbar menu button */
      editor.ui.registry.addMenuButton('componenttags', {
        text: this.$t('field.component_tags'),
        fetch: (callback) => {
          console.warn(this.getPreprocessedTags(this.tags, editor))
          callback(this.getPreprocessedTags(this.tags, editor))
        }
      });
    },
    /**
     * Force reload component
     */
    forceReload() {
      // Change isReady flag to "false" to destroy component
      this.isReady = false
      this.$nextTick(() => this.isReady = true)
    },
    /**
     *
     * @param tags
     * @param editor
     * @return {unknown[]}
     */
    getPreprocessedTags(tags, editor) {
      return tags.map(tag => {
        tag.text = tag.key
        tag.onAction = () => editor.insertContent(tag.key)
        if (!this.$_.isEmpty(tag.sub)) {
          tag.getSubmenuItems = () => this.getPreprocessedTags(tag.sub, editor)
        }
        return tag
      })
    }
  }
}
</script>
