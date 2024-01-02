<template>
  <div class="rich-text-editor">
    <Skeleton :model-value="!loading && isReady" :height="height">
      <Editor v-model="model" tinymce-script-src="tinymce/tinymce.min.js" :init="init"/>
    </Skeleton>
  </div>
</template>

<script>
import Editor from "@tinymce/tinymce-vue"
import Skeleton from "components/General/Other/Skeleton.vue";

import {mapState} from "pinia";
import {useEnvStore} from "stores/env";
import {uid} from "quasar";

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

  emits: ['update:modelValue'],

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
        this.$emit('update:modelValue', value)
      }
    },
    // Init configuration
    init() {
      return {
        plugins: 'preview importcss searchreplace autolink directionality code visualblocks visualchars fullscreen image link media codesample table charmap pagebreak nonbreaking anchor insertdatetime advlist lists wordcount help charmap quickbars emoticons',
        menubar: 'file edit view insert format tools table help',
        toolbar: 'undo redo | bold italic underline strikethrough | fontfamily fontsize blocks | alignleft aligncenter alignright alignjustify | outdent indent |  numlist bullist | forecolor backcolor removeformat | pagebreak | charmap emoticons | fullscreen  preview print | insertfile image media template link anchor code codesample | ltr rtl | componenttags',
        toolbar_sticky: this.stickyToolbar,
        image_advtab: true,
        importcss_append: true,
        height: this.height,
        image_caption: true,
        quickbars_selection_toolbar: 'bold italic underline strikethrough | quicklink blockquote quicktable',
        noneditable_class: 'printable-tag',
        toolbar_mode: 'sliding',
        contextmenu: 'link image table',
        skin: this.$q.dark.isActive ? 'oxide-dark' : 'oxide',
        content_css: [this.$q.dark.isActive ? 'dark' : 'default', '/src/css/tinymce_components.scss'],
        body_class: this.$q.dark.isActive ? 'body--dark' : 'body--light',
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
  },

  methods: {
    /**
     * Setup Editor
     *
     * @param {Editor} editor
     */
    setup(editor) {
      // Save editor reference
      this.editor = editor

      /* example, adding a toolbar menu button */
      this.editor.ui.registry.addMenuButton('componenttags', {
        text: this.$t('field.component_tags'),
        fetch: this.fetchMenuButton
      });

      // To intercept and handle printable tag copy or move
      this.editor.on('BeforeExecCommand', this.onBeforeInsertComponentTag)
      // Create MutationObserver to monitor deleted node
      const observer = new MutationObserver(mutations => {
        // Iterate through each mutation
        mutations.forEach(mutation => {
          // Iterate through each removed node
          mutation.removedNodes.forEach((node) => {
            // Check if tag name is span
            if (node.tagName === 'SPAN' && node.classList.contains('iterable-tag')) {
              this.onAfterDeleteIterableTag(node)
            }
            if (node.tagName === 'TR' && node.classList.contains('iterable-row')) {
              this.onAfterDeleteIterableTag(node)
            }
          })
        })
      })

      // Observe on Init
      this.editor.on('init', () => {
        observer.observe(this.editor.getBody(), {subtree: true, childList: true})
      })
      // Disconnect observer on Remove
      this.editor.on('remove', () => observer.disconnect())
    },

    /**
     * On Menu Button fetch event
     *
     * @param callback
     */
    fetchMenuButton(callback) {
      console.warn("hello")
      callback(this.getPreprocessedTags(this.tags))
    },

    /**
     * On before insert Component (or Printable) Tag
     *
     * @param {ExecCommandEvent} command
     */
    onBeforeInsertComponentTag(command) {
      // Retrieve value
      const value = command.value

      // Only care about non-manual mceInsertContent command
      if (command.command === 'mceInsertContent' && !value.manual) {
        // Check if that tag is copied and pasted
        if (value.paste) {
          // Stop event from running
          command.preventDefault()
          command.stopImmediatePropagation()
          command.stopPropagation()

          // Insert pasted content
          return this.editor.insertContent(this.updateTagsTimestamp(value.content), {manual: true})
        }

        // Get the dragged tag
        const printableTag = this.$util.div(value.content ?? value).querySelector('.printable-tag')
        // This means that tag is dragged and dropped
        if (!this.$util.isUnset(printableTag)) {
          // Stop event from running
          command.preventDefault()
          command.stopImmediatePropagation()
          command.stopPropagation()

          // This action must occur after id removal from onAfterDeleteIterableTag
          this.$nextTick(() => this.$nextTick(() => this.tryToInsertTag(this.searchTag(this.tags, printableTag.id.substring(39)))))
        }
      }
    },

    /**
     * On after delete Iterable Tag
     *
     * @param {Node} node
     */
    onAfterDeleteIterableTag(node) {
      // Get parent key
      const parentKey = node.className.slice(27).trim()
      // Find table with id of ${parentKey}
      const table = this.editor.dom.select(`table[id='${parentKey}']`)[0]
      // Check if table is set and there is no child with 'iterable-tag' class
      if (!this.$util.isUnset(table) && this.$util.isUnset(table.querySelector('.iterable-tag'))) {
        this.$nextTick(() => {
          // Clear table metadata
          table.id = ''
          table.classList.remove('wrapping-table')
          // Clear all class of <tr/>
          table.querySelectorAll('tr').forEach(el => el.classList.remove(...el.classList))
        }).catch(this.$error.log)
      }
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
     * Preprocess tag before giving to Tinymce
     *
     * @param {[PrintTag]} tags
     * @return {array}
     */
    getPreprocessedTags(tags) {
      // Iterate and map each tag
      return tags.map(tag => {
        // Set type
        tag.type = "menuitem"
        // Translate Tag's text (or label)
        tag.text = this.$t(`entity.${tag.key}`)
        // Add icon
        tag.icon = this.getTagIcon(tag)

        // Create Tag click's handler
        tag.onAction = () => this.tryToInsertTag(tag)
        // Check if Tag has sub Tags
        if (!this.$_.isEmpty(tag.sub)) {
          // Re-set type
          tag.type = "nestedmenuitem"
          // Preprocess sub tag recursively
          tag.getSubmenuItems = () => this.getPreprocessedTags(tag.sub)
        }

        return tag
      })
    },

    /**
     * Get tag's icon
     *
     * @param {PrintTag} tag
     * @return {string}
     */
    getTagIcon(tag) {
      // Output
      let icon = 'non-breaking'

      if (tag.isIdentifier) {
        icon = 'unlock'
      } else if (tag.isIterable || tag.isParentIterable) {
        icon = 'unordered-list'
      }

      return icon
    },

    /**
     * Try to insert tag
     *
     * @param {PrintTag} tag
     * @return {boolean}
     */
    tryToInsertTag(tag) {
      try {
        // Open an undo transaction
        this.editor.undoManager.transact(() => {
          // Uid
          const stamp = uid()
          // Backup content
          const content = this.editor.getContent()
          // Insert tag
          this.editor.insertContent(this.craftTagContent(tag, stamp), {manual: true})

          // Validate
          const message = this.validateTag(tag, stamp)
          if (!this.$util.isUnset(message)) {
            // Rollback content
            this.editor.setContent(content)
            // Throw error to prevent creating new Undo level
            throw new Error(message)
          }
        })
      } catch (e) {
        // Notify
        this.notify(e.message)
      }
    },

    /**
     * Craft tag content
     *
     * @param {PrintTag} tag
     * @param {string} stamp
     * @return {string}
     */
    craftTagContent(tag, stamp) {
      // Create extra class name
      let extraClassName = ''
      if (tag.isParentIterable) {
        // Check if tag is iterable
        extraClassName = `iterable-tag ${tag.parentKey}`
      } else {
        // For simple tag (just need to put data inside the tag)
        extraClassName = `simple-tag`
      }

      // Translate key
      const translatedKey = this.$t(`entity.${tag.key}`).replace(' ', '_')

      return `<span class="printable-tag ${extraClassName}" id="u-${stamp}-${tag.key}">{{${translatedKey}}}</span>`
    },

    /**
     * Validate tag
     *
     * @param {PrintTag} tag
     * @param {string} stamp
     * @return {null|string}
     */
    validateTag(tag, stamp) {
      // Select tag
      const tags = this.editor.dom.select(`#u-${stamp}-${tag.key}`)

      // No need to validate if not iterable
      if (!tag.isParentIterable) {
        return
      }

      // Check if this tag's parent is iterable, then get the nearest captured table
      const closestTable = tags[0].closest('table.wrapping-table') ?? tags[0].closest('table')
      // Get the wrapping table for tag is existing
      const wrappingTable = this.editor.dom.select(`table[id='${tag.parentKey}']`)

      // Check
      if (this.$util.isUnset(closestTable)) {
        // This means that tag is outside of table
        return this.$t('message.invalid_iterable_tag')
      } else if (wrappingTable.length === 0) {
        // This means that tag is inside of table but the table is not captured
        this.captureTag(tag, closestTable)
      } else if (closestTable.id !== wrappingTable[0].id) {
        // This means that tag is outside of it correct wrapping table
        return this.$t('message.invalid_iterable_area', {attr: this.$t(`entity.${tag.parentKey}`)})
      }

      // Capture the <tr/> so observer can observe it
      tags[0].closest('tr').classList.add('printable-row', 'iterable-row', tag.parentKey)
    },

    /**
     * Capture wrapping table
     * 1. Adding tag.parentKey as wrapping table id
     * 2. Adding 'wrapping-table' to wrapping table class list
     *
     * @param {PrintTag|null} tag
     * @param {HTMLElement} closestTable
     */
    captureTag(tag, closestTable) {
      if (!closestTable.classList.contains('wrapping-table')) {
        // Add id and class to closest (or wrapping) table
        closestTable.id = tag.parentKey
        closestTable.classList.add('wrapping-table')
      }
    },

    // ****************************
    // Utility
    // ****************************

    /**
     * Search tag by html string
     *
     * @param {[PrintTag]} tags
     * @param {string} key
     * @return {PrintTag} tag
     */
    searchTag(tags, key) {
      // Iterate through each tag
      for (const tag of tags) {
        // Check if tag.key matches key
        if (tag.key === key) {
          return tag
        }
        // If tag contains sub tags, search in sub tags
        if (Array.isArray(tag.sub) && tag.sub.length > 0) {
          const output = this.searchTag(tag.sub, key)
          // If output is not null, return it
          if (!this.$util.isUnset(output)) {
            return output
          }
        }
      }

      return null
    },

    /**
     * Update tags timestamp
     *
     * @param htmlString
     * @return {string}
     */
    updateTagsTimestamp(htmlString) {
      // Wrap html string with div
      const div = this.$util.div(htmlString)
      // Get all <span/> with class of printable-tag
      div.querySelectorAll('span.printable-tag').forEach(tag => {
        // Set new id
        tag.id = `u-${uid()}-${tag.id.substring(39)}`
      })

      return div.innerHTML
    },

    /**
     * Notify with Tinymce Notifier
     *
     * @param {string} message
     * @param {string} type
     */
    notify(message, type = 'error') {
      this.editor.notificationManager.open({
        text: message,
        timeout: 3000,
        type: type
      });
    }
  }
}
</script>
