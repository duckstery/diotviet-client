<template>
  <q-dialog ref="dialogRef" @hide="onHide">
    <q-card class="q-dialog-plugin tw-w-[900px]" style="max-width: 80vw;">
      <q-card-section>
        <div class="tw-text-lg tw-font-semibold">
          {{ mode === 'update' ? $t('field.update') : $t('field.create') }}
        </div>
      </q-card-section>
      <q-card-section>
        <div class="row">
          <div class="tw-mt-3 col-12 col-md-6 tw-px-2">
            <InputField
              :src="`images/title.png`"
              :label="$t(`field.title`)"
            >
              <template #default="props">
                <TextField v-model="input.title" v-bind="props" compact input-class="tw-p-0"/>
              </template>
            </InputField>

            <InputField
              :src="`images/code.png`"
              :label="$t(`field.code`)"
            >
              <template #default="props">
                <TextField v-model="input.code" v-bind="props" compact input-class="tw-p-0"
                           :placeholder="$t('message.blank_for_auto')"/>
              </template>
            </InputField>

            <InputField
              :src="`images/category.png`"
              :label="$t(`field.category`)"
            >
              <template #default="props">
                <q-select v-model="input.category" v-bind="props" dense :options="categories"
                          option-label="name" option-value="id" map-options emit-value use-chips/>
              </template>
            </InputField>

            <InputField
              :src="`images/groups.png`"
              :label="$t(`field.groups`)"
            >
              <template #default="props">
                <q-select v-model="input.groups" v-bind="props" dense :options="groups"
                          option-label="name" option-value="id" map-options emit-value use-chips multiple/>
              </template>
            </InputField>
          </div>

          <div class="tw-mt-3 col-12 col-md-6 tw-px-2">
            <InputField
              :src="`images/original_price.png`"
              :label="$t(`field.original_price`)"
              space
            >
              <template #default="props">
                <TextField v-model="input.originalPrice" v-bind="props"
                           compact required class="tw-w-48" input-class="tw-p-0 tw-text-center" mask="###,###,###,###"/>
              </template>
            </InputField>

            <InputField
              :src="`images/discount.png`"
              :label="$t(`field.discount`)"
              space
            >
              <template #before>
                <q-toggle
                  v-model="input.discountUnit"

                  true-value="cash"
                  false-value="%"
                  :icon="discountUnitIcon"
                  :label="discountUnitLabel"
                />
              </template>
              <template #default="props">
                <TextField v-model="input.discount" v-bind="props"
                           compact class="tw-w-48" input-class="tw-p-0 tw-text-center"
                           :mask="discountMask"/>
              </template>
            </InputField>

            <DisplayField
              space
              inner-class="tw-w-48"
              mask="###,###,###,###"
              :modelValue="input.actualPrice"
              :src="`images/actual_price.png`"
              :label="$t(`field.actual_price`)"
            />

            <InputField
              :src="`images/measure_unit.png`"
              :label="$t(`field.measure_unit`)"
            >
              <template #default="props">
                <TextField v-model="input.measureUnit" v-bind="props" compact input-class="tw-p-0"/>
              </template>
            </InputField>

            <InputField
              :src="`images/weight.png`"
              :label="$t(`field.weight`)"
            >
              <template #default="props">
                <TextField v-model="input.weight" v-bind="props" compact suffix="Kg" mask="#,###"/>
              </template>
            </InputField>
          </div>

          <div class="tw-mt-3 col-12 tw-px-2">
            <InputField
              :src="`images/image.png`"
              :label="$t(`field.image`)"
              horizontal
            >
              <template #default="props">
                <UploadMage v-model="file" v-bind="props" :max-size="5242880"/>
              </template>
            </InputField>
          </div>

        </div>
      </q-card-section>
      <q-card-actions align="right">
        <Button :label="$t('field.confirm')" icon="fa-solid fa-check"
                stretch color="positive" class="tw-ml-2" no-caps @click="onConfirm"/>
        <Button :label="$t('field.cancel')" icon="fa-solid fa-xmark"
                stretch color="negative" class="tw-ml-2" no-caps @click="onCancel"/>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<script>
import {useDialogPluginComponent} from 'quasar'
import {reactive} from 'vue'
import {usePriceControl} from "src/composables/usePriceControl";
import {useRangeControl} from "src/composables/useRangeControl";

import InputField from "components/General/Other/InputField.vue";
import Button from "components/General/Other/Button.vue";
import DisplayField from "components/General/Other/DisplayField.vue";
import TextField from "components/General/Other/TextField.vue";
import UploadMage from "components/General/Other/UploadMage.vue";

export default {
  name: 'ProductEditor',
  components: {UploadMage, TextField, InputField, DisplayField, Button},
  props: {
    // Editor mode: 'create', 'update', 'copy'
    mode: String,
    // Categories
    categories: Array,
    // Groups
    groups: Array,
    // Target item
    item: {
      type: Object,
      default: () => ({
        id: null,
        title: null,
        code: null,
        category: null,
        groups: [],
        originalPrice: '1000',
        discount: '0',
        discountUnit: "%",
        actualPrice: '1000',
        measureUnit: null,
        src: null,
        isInBusiness: true,
        canBeAccumulated: false,
        weight: '1',
        description: null,
      })
    }
  },

  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup(props) {
    // REQUIRED; must be called inside of setup()
    const {dialogRef, onDialogHide, onDialogOK, onDialogCancel} = useDialogPluginComponent()
    // dialogRef      - Vue ref to be applied to QDialog
    // onDialogHide   - Function to be used as handler for @hide on QDialog
    // onDialogOK     - Function to call to settle dialog with "ok" outcome
    //                    example: onDialogOK() - no payload
    //                    example: onDialogOK({ /*.../* }) - with payload
    // onDialogCancel - Function to call to settle dialog with "cancel" outcome

    // Make a reactive input
    const input = reactive({...props.item})
    // Put range control on weight
    useRangeControl(input, 'weight', 1000, 1)

    return {
      ...usePriceControl(input, 'originalPrice', 'actualPrice'),
      // This is REQUIRED;
      // Need to inject these (from useDialogPluginComponent() call)
      // into the vue scope for the vue html template
      input,
      dialogRef,
      onHide: onDialogHide,
      onCancel: onDialogCancel,
      ok: onDialogOK,
    }
  },

  data: () => ({
    file: []
  }),

  methods: {
    onConfirm() {
      console.warn(this.input)
      this.ok()
    },
  }
}
</script>
