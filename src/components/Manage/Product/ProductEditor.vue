<template>
  <q-dialog ref="dialogRef" @hide="onHide">
    <q-card class="q-dialog-plugin tw-w-[900px] virtual-scrollbar" style="max-width: 80vw;">
      <q-card-section>
        <div class="tw-text-lg tw-font-semibold">
          {{ mode === 'update' ? $t('field.update') : $t('field.create') }}
        </div>
      </q-card-section>
      <q-card-section>
        <div class="row">
          <div class="tw-mt-3 col-12 col-md-6 tw-pr-5">
            <InputField
              v-for="key in ['code', 'title', 'category', 'groups']"
              :src="`images/${key}.png`"
              :label="$t(`field.${key}`)"
              :vuelidate="v$.input[key]"
            >
              <template #default="props">
                <TextField
                  v-if="['code', 'title'].includes(key)" v-bind="props" v-model="v$.input[key].$model"
                  compact input-class="tw-p-0"
                  :placeholder="key === 'code' ? $t('message.blank_for_auto') : ''"
                />
                <q-select
                  v-else v-bind="props" v-model="input[key]"
                  dense
                  map-options emit-value use-chips
                  :options="key === 'category' ? categories : groups" option-label="name" option-value="id"
                  :multiple="key === 'groups'"
                />
              </template>
            </InputField>
          </div>

          <div class="tw-mt-3 col-12 col-md-6 tw-pr-5">
            <InputField
              v-for="key in ['originalPrice', 'discount']"
              space
              :src="`images/${$util.camelToSnake(key)}.png`"
              :label="$t(`field.${$util.camelToSnake(key)}`)"
              :vuelidate="v$.input[key]"
            >
              <template #before v-if="key === 'discount'">
                <q-toggle
                  v-model="v$.input.discountUnit.$model"

                  true-value="cash"
                  false-value="%"
                  :icon="discountUnitIcon"
                  :label="discountUnitLabel"
                />
              </template>
              <template #default="props">
                <TextField
                  v-model="v$.input[key].$model" v-bind="props"
                  compact class="tw-w-48" input-class="tw-p-0 tw-text-center"
                  :mask="key === 'discount' ? discountMask : '###,###,###,###'"
                />
              </template>
            </InputField>

            <DisplayField
              space
              inner-class="tw-w-48"
              mask="###,###,###,###"
              :modelValue="input.actualPrice"
              :src="`images/actual_price.png`"
              :label="$t(`field.actual_price`)"
              class="tw-mb-5"
            />

            <InputField
              v-for="key in ['measureUnit', 'weight']"
              :src="`images/${$util.camelToSnake(key)}.png`"
              :label="$t(`field.${$util.camelToSnake(key)}`)"
              :vuelidate="v$.input[key]"
            >
              <template #default="props">
                <TextField
                  v-if="key === 'measureUnit'" v-model="input.measureUnit" v-bind="props"
                  compact input-class="tw-p-0"
                />
                <TextField
                  v-else-if="key === 'weight'" v-model="input.weight" v-bind="props"
                  compact suffix="Kg" mask="#,###"/>
              </template>
            </InputField>
          </div>

          <div v-for="key in ['file', 'description']" class="tw-mt-3 col-12">
            <InputField
              horizontal
              :src="`images/${key === 'file' ? 'image' : 'note'}.png`"
              :label="$t(`field.${key === 'file' ? 'image' : key}`)"
              :vuelidate="v$.input[key]"
            >
              <template #default="props">
                <UploadMage
                  v-if="key === 'file'" v-model="v$.input[key].$model" v-bind="props"
                  :max-size="5242880"
                />
                <RichTextField
                  v-else v-model="v$.input[key].$model" v-bind="props"
                />
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
import {reactive, toRef} from 'vue'
import {usePriceControl} from "src/composables/usePriceControl";
import {useRangeControl} from "src/composables/useRangeControl";
import {useDialogEditor} from "src/composables/useDialogEditor";
import {required} from '@vuelidate/validators'

import InputField from "components/General/Other/InputField.vue";
import Button from "components/General/Other/Button.vue";
import DisplayField from "components/General/Other/DisplayField.vue";
import TextField from "components/General/Other/TextField.vue";
import UploadMage from "components/General/Other/UploadMage.vue";
import RichTextField from "components/General/Other/RichTextField.vue";

export default {
  name: 'ProductEditor',
  components: {RichTextField, UploadMage, TextField, InputField, DisplayField, Button},
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
        title: "",
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
        description: "",
      })
    }
  },

  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup(props) {
    // Make a reactive input
    const input = reactive({...props.item})
    // Put range control on weight
    useRangeControl(toRef(input, 'weight'), 1000, 1)

    return {
      input,
      ...usePriceControl(input, 'originalPrice', 'actualPrice'),
      ...useDialogEditor(input, props.mode)
    }
  },

  validations() {
    return {
      input: {
        code: {},
        title: {required},
        category: {required},
        groups: {},
        originalPrice: {required},
        discount: {required},
        discountUnit: {required},
        measureUnit: {},
        file: {},
        // isInBusiness: true,
        // canBeAccumulated: false,
        weight: {},
        description: {},
      }
    }
  },
}
</script>
