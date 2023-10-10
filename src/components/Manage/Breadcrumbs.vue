<template>
  <q-breadcrumbs>
    <template v-slot:separator>
      <q-icon
        size="1.5em"
        name="chevron_right"
        color="primary"
      />
    </template>

    <q-breadcrumbs-el to="/work" icon="fa-solid fa-store" :label="$t('field.home')"/>
    <q-breadcrumbs-el
      v-for="item in items"
      :to="item.to"
      :icon="`fa-solid ${item.icon}`"
      :label="item.label"
    />
  </q-breadcrumbs>
</template>

<script>
export default {
  name: 'Breadcrumbs',

  props: {
    // Breadcrumbs items
    items: {
      type: Array,
      default: () => ([])
    }
  }
}
</script>
