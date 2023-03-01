<template>
  <div>
    <v-alert dense border="left" colored-border color="main" :elevation="elevate ? 2 : 0">
      <template #prepend>
        <img v-if="imgSrc" :src="imgSrc" alt="" style="width: 24px; height: 24px"/>
        <v-icon v-else color="main">{{ icon }}</v-icon>
      </template>
      <h3 class="tw-ml-1">{{ title }}</h3>
      <template v-if="redirectTitle" #append>
        <FakeButton override right icon="arrow-right-thin" color="link" :to="to">{{ redirectTitle }}</FakeButton>
      </template>
    </v-alert>

    <slot/>
  </div>
</template>

<script>
import FakeButton from "~/components/General/Other/FakeButton";

export default {
  name: "Section",
  components: {FakeButton},
  props: {
    /**
     * Use image to replace icon (prioritize)
     */
    imgSrc: {
      type: String,
      default: ''
    },
    /**
     * Icon
     */
    icon: {
      type: String,
      default: 'mdi-fire'
    },
    /**
     * Section title
     */
    title: {
      type: String,
      default: 'Title'
    },
    /**
     * Redirect title
     */
    redirectTitle: {
      type: String,
      default: ''
    },
    /**
     * Section redirect URL
     */
    to: {
      type: String,
      default: '/'
    },
    /**
     * Allow shadow
     */
    elevate: {
      type: Boolean,
      default: false
    }
  }
}
</script>
