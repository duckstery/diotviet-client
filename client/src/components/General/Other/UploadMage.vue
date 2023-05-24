<template>
  <q-file
    v-bind="$attrs"
    outlined
    ref="picker"

    :model-value="modelValue"
    :multiple="multiple"
    :max-file-size="size"
    :label="$t('message.pick_file')"
    accept="image/png,image/jpg,image/jpeg"

    @rejected="onRejected"
    @update:model-value="$emit('update:model-value', $event)"
  >
    <template v-slot:file="{ index, file }">
      <div
        class="tw-rounded-md tw-border-solid tw-border tw-m-2 tw-border-neutral-500 tw-max-w-[130px] tw-max-h-[130px]">
        <div class="tw-absolute z-max tw-w-[128px] tw-float">
          <Button
            flat
            size="sm"
            icon="close"
            color="white"
            class="tw-float-right"
            @click.prevent="onCancelFile(index)"
          />
        </div>
        <q-img
          :src="getSrc(file)"
          spinner-color="gear"
          spinner-size="82px"
          height="128px"
          width="128px"
          class="tw-rounded-md"
        >
          <div
            class="absolute-top-left tw-max-h-[50px] tw-w-[128px] ellipsis overflow-hidden-y tw-rounded-t-md"
            style="max-width: 128px; padding: 12px"
          >
            {{ file.name }}
          </div>
        </q-img>
      </div>
    </template>
  </q-file>
</template>

<script>
import Button from "components/General/Other/Button.vue";

export default {
  name: "UploadMage",
  components: {Button},
  props: {
    // Model value
    modelValue: Array,
    // Max size
    maxSize: Number,
    // Allow multiple file
    multiple: Boolean,
    // Use component native notifier
    nativeNotify: Boolean
  },

  emits: ['update:model-value'],

  methods: {
    /**
     * On file rejected
     */
    onRejected() {
      // Notify plugin needs to be installed
      // https://quasar.dev/quasar-plugins/notify#Installation
      if (!this.nativeNotify) {
        this.$notifyErr(this.$t('message.invalid_file'))
      }
    },

    /**
     * On cancel a file
     *
     * @param index
     */
    onCancelFile(index) {
      this.$refs.picker.removeAtIndex(index)
    },

    /**
     * Convert File to src
     *
     * @param file
     * @returns {string}
     */
    getSrc(file) {
      return URL.createObjectURL(file)
    }
  }
}
</script>

<style scoped>

</style>
