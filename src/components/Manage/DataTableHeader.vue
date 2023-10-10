<template>
  <q-tr>
    <q-th auto-width>
      <q-checkbox size="xs" v-model="props.selected"/>
    </q-th>
    <q-th
      v-for="col in props.cols"
      :key="col.name"
      :props="props"
    >
      <span class="tw-text-sm">{{ col.label }}</span>
    </q-th>
  </q-tr>
</template>

<script>
export default {
  name: 'DataTableHeader',

  props: {
    props: {
      type: Object,
      default: () => ({
        cols: [],
        expand: false,
        selected: false,
      })
    }
  }
}
</script>
