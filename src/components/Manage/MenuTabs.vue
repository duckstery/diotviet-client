<template>
  <q-tabs
    dense
    no-caps
    inline-label
    mobile-arrows
  >
    <q-route-tab
      v-for="tab in tabs"
      exact
      :to="tab.to"
      :icon="`fa-solid ${tab.icon}`"
      :label="$t(`field.${tab.key}`)"
    />
  </q-tabs>
</template>

<script>
export default {
  name: "MenuTabs",

  props: {
    tabs: Array,
  },
}
</script>
