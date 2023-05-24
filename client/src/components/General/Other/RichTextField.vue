<template>
  <q-editor
    :model-value="modelValue"
    v-bind="$attrs"

    dense
    :toolbar="toolbar"
    :readonly="readonly"
  />
</template>

<script>
export default {
  name: "RichTextField",

  props: {
    modelValue: String,
    readonly: Boolean
  },

  computed: {
    toolbar() {
      return [
        ['bold', 'italic', 'strike', 'underline', 'subscript', 'superscript'],
        ['hr', 'link'],
        [
          {
            label: this.$q.lang.editor.fontSize,
            icon: this.$q.iconSet.editor.fontSize,
            fixedLabel: true,
            fixedIcon: true,
            list: 'no-icons',
            options: [
              'size-1',
              'size-2',
              'size-3',
              'size-4',
              'size-5'
            ]
          },
          'removeFormat'
        ],
        ['quote', 'unordered', 'ordered'],
        ['undo', 'redo'],
      ]
    }
  }
}
</script>

<style scoped>

</style>
