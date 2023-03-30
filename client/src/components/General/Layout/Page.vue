<template>
  <q-page class="bg-grey-3">
    <div class="bg-grey-3 tw-p-5 tw-pr-20" :style="style">
      <slot/>
    </div>
  </q-page>
</template>

<script>
export default {
  name: 'Page',

  inject: ['globalVars'],

  computed: {
    style() {
      return {
        minHeight: `${this.globalVars.minHeight}px`
      }
    }
  }
}
</script>
