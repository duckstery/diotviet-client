<template>
  <div v-if="false" v-bind="$attrs"/>
  <!-- Business operations -->
  <DropdownButton v-if="!noImport" :label="$t('field.import')" icon="fa-solid fa-file-import"
                  stretch color="positive" class="tw-ml-2" no-caps>
    <q-list>
      <q-item clickable v-close-popup @click="onImport(false)">
        <q-item-section>{{$t('message.from', {attr: 'CSV'})}}</q-item-section>
      </q-item>
      <q-item v-if="legacy" clickable v-close-popup @click="onImport(true)">
        <q-item-section>{{$t('message.from', {attr: 'KiotViet'})}}</q-item-section>
      </q-item>
    </q-list>
  </DropdownButton>
  <q-file v-show="false" dense borderless hide-bottom-space hide-hint :max-file-size="5242880"
          accept="text/csv,application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
          ref="picker" @rejected="onRejectFile" @update:model-value="onPickFile"/>
  <Button :label="$t('field.export')" icon="fa-solid fa-file-export" @click="onExport"
          stretch color="positive" class="tw-ml-2" no-caps/>
</template>

<script>
import Button from "components/General/Other/Button.vue";
import DropdownButton from "components/General/Other/DropdownButton.vue";

export default {
  name: "ImEx",

  components: {DropdownButton, Button},

  props: {
    // Enable legacy import
    legacy: Boolean,
    // Disable import
    noImport: Boolean
  },

  emits: ['request'],

  data: () => ({
    onLegacyImport: false
  }),

  methods: {
    /**
     * On import file
     */
    onImport(legacy = false) {
      this.onLegacyImport = legacy
      this.$refs.picker.pickFiles()
    },

    /**
     * On import file is rejected due to invalidity
     */
    onRejectFile() {
      this.$notifyErr(this.$t('message.invalid_file'))
    },

    /**
     * On import file is picked successfully
     *
     * @param file
     */
    onPickFile(file) {
      this.$emit('request', this.onLegacyImport ? 'legacy' : 'import', file)
    },

    /**
     * On export file
     */
    onExport() {
      this.$emit('request', 'export')
    },
  }
}
</script>
