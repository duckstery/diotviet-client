<template>
  <div class="rich-text-editor">
    <CKEditor :model-value="modelValue" :editor="editor" :config="config" :disabled="readonly"
              @ready="onReady" @input="onInput"/>
  </div>
</template>

<script>
import CKEditor from "@ckeditor/ckeditor5-vue"

import {ClassicEditor} from "@ckeditor/ckeditor5-editor-classic"
import {Alignment} from "@ckeditor/ckeditor5-alignment"
import {Autoformat} from "@ckeditor/ckeditor5-autoformat"
import {Bold, Italic, Underline, Strikethrough, Subscript, Superscript} from "@ckeditor/ckeditor5-basic-styles"
import {Essentials} from "@ckeditor/ckeditor5-essentials"
import {FontColor, FontSize, FontFamily} from "@ckeditor/ckeditor5-font"
import {GeneralHtmlSupport} from "@ckeditor/ckeditor5-html-support";
import {Heading} from "@ckeditor/ckeditor5-heading"
import {Highlight} from "@ckeditor/ckeditor5-highlight"
import {Image, ImageUpload} from "@ckeditor/ckeditor5-image"
import {Indent} from "@ckeditor/ckeditor5-indent"
import {Link} from "@ckeditor/ckeditor5-link"
import {List} from "@ckeditor/ckeditor5-list"
import {Paragraph} from "@ckeditor/ckeditor5-paragraph"
import {PasteFromOffice} from "@ckeditor/ckeditor5-paste-from-office"
import {RemoveFormat} from "@ckeditor/ckeditor5-remove-format"
import {SelectAll} from "@ckeditor/ckeditor5-select-all"
import {SourceEditing} from "@ckeditor/ckeditor5-source-editing";
import {SpecialCharacters} from "@ckeditor/ckeditor5-special-characters"
import {Table, TableCellProperties, TableColumnResize, TableProperties, TableToolbar} from "@ckeditor/ckeditor5-table"
import {TextTransformation} from "@ckeditor/ckeditor5-typing"
import {Undo} from "@ckeditor/ckeditor5-undo";

export default {
  name: "RichTextEditor",

  components: {
    CKEditor: CKEditor.component
  },

  props: {
    // Model
    modelValue: String,
    // Readonly mode
    readonly: Boolean,
    // Padding top with toolbar height
    padding: Boolean,
    // Editor height
    height: {
      type: String,
      default: '500px'
    }
  },

  emits: ['update:model-value'],

  data: () => ({
    instance: null,
    editor: ClassicEditor,
    config: {
      plugins: [
        Alignment,
        Autoformat,
        Bold,
        Essentials,
        FontColor,
        FontSize,
        FontFamily,
        GeneralHtmlSupport,
        Heading,
        Highlight,
        Image,
        ImageUpload,
        Indent,
        Italic,
        Link,
        List,
        Paragraph,
        PasteFromOffice,
        RemoveFormat,
        SelectAll,
        SourceEditing,
        SpecialCharacters,
        Strikethrough,
        Subscript,
        Superscript,
        Table,
        TableCellProperties,
        TableColumnResize,
        TableProperties,
        TableToolbar,
        TextTransformation,
        Underline,
        Undo
      ],

      toolbar: {
        items: [
          'heading',
          'bold',
          'italic',
          'underline',
          'link',
          'bulletedList',
          'numberedList',
          '|',
          'outdent',
          'indent',
          '|',
          'specialCharacters',
          'imageUpload',
          'insertTable',
          'undo',
          'redo',
          '|',
          'fontFamily',
          'fontColor',
          'fontSize',
          'alignment',
          'highlight',
          '|',
          'subscript',
          'superscript',
          'strikethrough',
          'removeFormat',
          '|',
          'selectAll',
          'sourceEditing'
        ],
        shouldNotGroupWhenFull: true
      },
      htmlSupport: {
        allow: [
          {
            name: 'style',
            attributes: false,
            classes: false,
            styles: false
          }
        ],
        disallow: [
          {
            name: /[\s\S]+/,    // For every HTML feature,
            attributes: {
              key: /^on.*$/     // disable 'on*' attributes, like 'onClick', 'onError' etc.
            }
          }
        ]
      },
      table: {
        contentToolbar: [
          'tableColumn',
          'tableRow',
          'mergeTableCells',
          'tableProperties',
          'tableCellProperties'
        ]
      }
    }
  }),

  computed: {
    isReady() {
      return !this.$util.isUnset(this.instance)
    }
  },

  watch: {
    // Toggle toolbar if readonly value is changed
    readonly() {
      if (this.isReady) {
        this.toggleToolbar()
      }
    },
  },

  methods: {
    /**
     * On editor's ready event
     *
     * @param instance
     */
    onReady(instance) {
      // Save instance
      this.instance = instance
      // Toggle toolbar
      this.toggleToolbar()
    },

    /**
     * On editor's input event
     * Remove <figure> and move <figure> width and height to table
     *
     * @param data
     */
    onInput(data) {
      // Create div container and setup innerHTMl
      const div = document.createElement('div')
      div.innerHTML = data
      // Find all figure tag with table tag
      div.querySelectorAll('figure > table').forEach(this.moveOut)
      this.$emit('update:model-value', div.innerHTML)
      // Remove container
      div.remove()
    },

    /**
     * Move the child out
     *
     * @param {HTMLElement} el
     */
    moveOut(el) {
      // Get root of el (<figure>)
      const figure = el.parentElement
      // First, inherit width and height of <figure> to el
      el.style.width = figure.style.width
      el.style.height = figure.style.height
      // Then, move the el out
      figure.parentElement.insertBefore(el, figure)
      // Finally, remove <figure>
      figure.remove()
    },

    /**
     * Setup padding
     */
    toggleToolbar() {
      if (this.padding) {
        // Get toolbar height
        const toolbarHeight = this.instance.ui.view.toolbar.element.getBoundingClientRect().height
        console.warn(toolbarHeight)
        // Add padding to editor
        this.instance.ui.view.editable.element.style.marginTop = `${toolbarHeight}px`
      }

      // Toggle toolbar
      this.instance.ui.view.toolbar.element.style.display = this.readonly ? 'none' : 'flex'
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.ck-editor__editable){
  min-height: v-bind(height) !important;
  max-height: v-bind(height) !important;

  &::-webkit-scrollbar-track {
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, 0.3);
    border-radius: 10px;
    background-color: #F5F5F5;
  }

  &::-webkit-scrollbar {
    width: 6px;
    height: 6px;
    border-radius: 10px;
    background-color: #F5F5F5;
  }

  &::-webkit-scrollbar-thumb {
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0, 0, 0, .3);
    background-color: #555;
  }
}
</style>
