<template>
  <div :id="id" :class="wrapperClasses" @mouseenter="isActive = true" @mouseleave="isActive = false" @click="goToEl">
    <slot class="tw-float-right">{{ content }}</slot>
    <transition appear enter-active-class="animated fadeIn" leave-active-class="animated fadeOut">
      <q-icon v-show="isActive" :name="icon" :size="size" :class="iconClasses" :color="color"/>
    </transition>
  </div>
</template>

<script lang="ts">

import {computed, ref, toRef} from "vue";
import {useRouter} from "vue-router";
import {util} from "src/boot";

export default {
  name: "Anchor",

  props: {
    // Icon
    icon: {
      type: String,
      default: "fa-solid fa-link",
    },
    // Size
    size: {
      type: String,
      default: "md"
    },
    // Color
    color: {
      type: String,
      default: 'grey-7'
    },
    // Href
    el: String,
    // Content
    content: String,
    // Setup id and href
    alias: String,
    // Put icon to front
    reverse: Boolean
  },

  setup(props, {attrs}) {
    // Router
    const router = useRouter()
    // Props to ref
    const el = toRef(props, 'el')
    const alias = toRef(props, 'alias')
    const reverse = toRef(props, 'reverse')

    return {
      // ID of Anchor
      id: computed(() => util.isUnset(util.nullIfEmpty(alias.value)) ? attrs.id : alias.value),
      // Check if Anchor is active (hovering)
      isActive: ref(false),
      // Go to Anchor target
      goToEl: () => router.push({hash: util.isUnset(util.nullIfEmpty(alias.value)) ? el.value : `#${alias.value}`}),
      // Anchor's wrapper class
      wrapperClasses: computed(() => ({
        'tw-cursor-pointer': true,
        'tw-flex': true,
        'tw-flex-row': true,
        'tw-flex-row-reverse': reverse.value,
        'tw-float-right': reverse.value,
      })),
      // Anchor's icon class
      iconClasses: computed(() => ({
        'tw-my-auto': true,
        'tw-ml-5': !reverse.value,
        'tw-mr-5': reverse.value
      }))
    }
  }

}
</script>


