<template>
  <div class="rich-text-editor">
    <Skeleton :model-value="!loading && isReady" :height="height">
      <Editor v-model="model" tinymce-script-src="/tinymce/tinymce.min.js" :init="init"/>
    </Skeleton>
  </div>
</template>

<script>
import Editor from "@tinymce/tinymce-vue"
import Skeleton from "components/General/Other/Skeleton.vue";

import {mapState} from "pinia";
import {useEnvStore} from "stores/env";

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
            // Special treatment for <tr/> and <table/>
            if (node.tagName === 'TR' || node.tagName === 'TABLE') {
              // Get classList reference
              const cl = node.classList
              if (cl.contains('iterable-row') || cl.contains('wrapping-table') || cl.contains('sub-wrapping-table')) {
                console.warn(node)
              }
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
        // Try to get printable tag
        const printableTag = this.getPrintableTag(value.content ?? value)
        // Only care if mceInsertContent is inserting printable tag
        if (!this.$util.isUnset(printableTag)) {
          // Stop event from running
          command.preventDefault()
          command.stopImmediatePropagation()
          command.stopPropagation()

          // Check
          if (value.paste) {
            // This means that tag is copied and pasted
            this.tryToInsertTag(this.searchTag(this.tags, printableTag.id) ?? {})
          } else {
            // This means that tag is dragged and dropped
            // This action must occur after id removal from onAfterDeleteIterableTag
            this.$nextTick(() => this.$nextTick(() => this.tryToInsertTag(this.searchTag(this.tags, printableTag.id) ?? {})))
          }
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
        })
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
          // Backup content
          const content = this.editor.getContent()
          // Generate timestamp
          const timestamp = (new Date()).getTime()
          // Insert tag
          this.editor.insertContent(this.craftTagContent(tag, timestamp), {manual: true})

          // Validate
          const message = this.validateTag(tag, timestamp)
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
     * @param {number} timestamp
     * @return {string}
     */
    craftTagContent(tag, timestamp) {
      // Create extra class name
      let extraClassName = ''
      if (tag.isParentIterable) {
        // Check if tag is iterable
        extraClassName = `iterable-tag of-${tag.parentKey}`
      } else {
        // For simple tag (just need to put data inside the tag)
        extraClassName = `simple-tag`
      }

      // Translate key
      const translatedKey = this.$t(`entity.${tag.key}`).replace(' ', '_')

      return `<span class="printable-tag ${extraClassName}" id="t${timestamp}-${tag.key}">{{${translatedKey}}}</span>`
    },

    /**
     * Validate tag
     *
     * @param {PrintTag} tag
     * @param {number} timestamp
     * @return {null|string}
     */
    validateTag(tag, timestamp) {
      // Select tag
      const tags = this.editor.dom.select(`#t${timestamp}-${tag.key}`)

      // No need to validate if this tag is not iterable
      if (!tag.isParentIterable) {
        return null;
      }

      // Check if this tag's parent is iterable, then get the nearest captured table
      const closestTable = tags[0].closest('table')
      // Get the wrapping table for tag is exists
      const wrappingTable = this.editor.dom.select(`table[id='${tag.parentKey}']`)

      // Check
      if (this.$util.isUnset(closestTable)) {
        // This means that tag is outside of table
        return this.$t('message.invalid_iterable_tag')
      } else if (wrappingTable.length === 0) {
        // Check  if closest table is not a sub wrapper
        return closestTable.classList.contains('sub-wrapping-table')
          // This means that closest table is a sub wrapper
          ? this.$t('message.invalid_iterable_area', {attr: this.$t(`entity.${tag.parentKey}`)})
          // This means that tag is inside of table but the table is not captured
          : this.tryToCaptureTag(tag, tags[0], closestTable)
      } else if (closestTable.id !== '' && closestTable.id !== wrappingTable[0].id) {console.warn('ahihi')
        // This means that tag is placed in another iterable area (not it valid iterable area)
        return this.$t('message.invalid_iterable_area', {attr: this.$t(`entity.${tag.parentKey}`)})
      } else if (closestTable.id === '') {
        // Check if wrapping table contains closest table
        return wrappingTable[0].contains(closestTable)
          // This means that tag is placed in an empty table inside it valid iterable area
          ?  this.tryToMarkTagSubTable(tag, tags[0], closestTable)
          // This means that tag is outside of it valid iterable tag
          :  this.$t('message.invalid_iterable_area', {attr: this.$t(`entity.${tag.parentKey}`)})
      }

      return null
    },

    /**
     * Check before capture
     *
     * @param {PrintTag} tag
     * @param {HTMLElement} el
     * @param {HTMLElement} closestTable
     */
    tryToCaptureTag(tag, el, closestTable) {
      // Find the current level of tag
      const requiredLevel = this.getTagLevel(tag)
      if (requiredLevel < 0) return
      // To check if a new tag with new parentKey is added to a wrapping-table
      for (const className of closestTable.classList) {
        // Only care about className that start with 'wrapping-table'
        if (className.startsWith('wrapping-table-level')) {
          // If tag is placed at correct level of correct parent tag, no need to capture anything
          // Else, notify error
          return className === `wrapping-table-level-${requiredLevel}` && closestTable.id === tag.parentKey
            ? null
            : this.$t('message.invalid_iterable_area', {attr: this.$t(`entity.${tag.parentKey}`)})
        }
      }

      // Capture
      this.captureTag(tag, el, closestTable)
    },

    /**
     * Capture wrapping table
     * 1. Adding tag.parentKey as wrapping table id
     * 2. Adding 'wrapping-table-level' as wrapping table level <tr/>
     *
     * @param {PrintTag} tag
     * @param {HTMLElement} el
     * @param {HTMLElement} closestTable
     */
    captureTag(tag, el, closestTable) {
      // Table tree
      const tree = []
      // Find the current level of tag
      const requiredLevel = this.getTagLevel(tag)
      // Check table tree level
      for (let level = requiredLevel; level >= 0; level--) {
        // Prepare data to capture tables
        tree.unshift({tag: tag, table: closestTable})
        // Get upper level tag and closestTable
        tag = this.searchTag(this.tags, tag.parentKey)
        closestTable = closestTable.parentElement.closest('table')

        // Check if table is placed at correct table level
        if (level > 0 && this.$util.isUnset(closestTable)) {
          return this.$t('message.invalid_iterable_area_level', {attr: level + 1})
        }
      }

      // Get parent wrapper (owner)
      let parentWrapper = {parentKey: 'none'}
      // Capture table
      for (const [level, items] of tree.entries()) {
        // Get tag and table
        const {tag, table} = items
        // Add id and class to closest (or wrapping) table
        table.id = tag.parentKey
        table.classList.add('wrapping-table', `wrapping-table-level-${level}`, `of-${parentWrapper.parentKey}`)
        // This tag will be parent wrapper (owner) of next level
        parentWrapper = tag
      }

      // Capture <td> for watch
      el.closest('tr').classList.add('iterable-row',  `of-${parentWrapper.parentKey}`)
    },

    /**
     * Check before mark
     *
     * @param {PrintTag} tag
     * @param {HTMLElement} el
     * @param {HTMLElement} closestTable
     */
    tryToMarkTagSubTable(tag, el, closestTable) {
      // Get closestTable classList
      const cTClassList = closestTable.classList
      // Check if this is not a valid sub table of tag if this table is marked
      if (cTClassList.contains('sub-wrapping-table') && !cTClassList.contains(`of-${tag.parentKey}`)) {
        // Get sub table owner
        for (let i = 0; i < cTClassList.length; i++) {
          // The template of owner is 'of-[owner's key]'
          if (cTClassList.item(i).startsWith('of-')) {
            // Get owner's key
            const ownerKey = cTClassList.item(i).substring(3)
            // Check if [belonging's key] === parentKey
            return ownerKey === tag.parentKey
              ? null
              // This means trying to add tag to a not owned sub table
              : this.$t('message.table_belongs_to', {attr: this.$t(`entity.${ownerKey}`)})
          }
        }
      } else {
        // Get tag required level
        const requiredLevel = this.getTagLevel(tag)
        // Get the closest wrapping table (closest wrapper)
        const closestWrapper = closestTable.closest('.wrapping-table')
        // Check closest wrapper's level
        if (!closestWrapper.classList.contains(`wrapping-table-level-${requiredLevel}`)) {
          // This means trying to add lower level tag to higher level sub table (empty)
          return this.$t('message.table_belongs_to', {attr: this.$t(`entity.${closestWrapper.id}`)})
        }

        this.markTagSubTable(tag, el, closestTable)
      }
    },

    /**
     * Mark table as sub table
     *
     * @param {PrintTag} tag
     * @param {HTMLElement} el
     * @param {HTMLElement} closestTable
     */
    markTagSubTable(tag, el, closestTable) {
      closestTable.classList.add('sub-wrapping-table', `of-${tag.parentKey}`)
      el.closest('tr').classList.add('iterable-row',  `of-${tag.parentKey}`)
    },

    // ****************************
    // Utility
    // ****************************

    /**
     * Search tag by html string
     *
     * @param {[PrintTag]} tags
     * @param {string} key
     * @return {PrintTag|null} tag
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
     * Get tag level
     *
     * @param tag
     */
    getTagLevel(tag) {
      return (tag.path.match(/\.\{i}/g) || []).length - 1
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
    },

    /**
     * Get printable tag from html string
     *
     * @param htmlString
     * @return {Element}
     */
    getPrintableTag(htmlString) {
      return this.$util.div(htmlString).querySelector('.printable-tag')
    }
  }
}
</script>
