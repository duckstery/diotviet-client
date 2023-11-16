<template>
  <q-dialog ref="dialogRef" @hide="onHide">
    <q-card class="q-dialog-plugin tw-w-[400px] virtual-scrollbar" style="max-width: 80vw;">
      <q-card-section>
        <div class="tw-text-lg tw-font-semibold">
          {{ mode === 'update' ? $t('field.update') : $t('field.create') }}
        </div>
      </q-card-section>
      <q-card-section>
        <div class="row">
          <div class="tw-mt-3 col-12">
            <InputField
              src="images/name.png" :label="$t(`field.name`)" :vuelidate="v$.input.name"
            >
              <template #default="props">
                <TextField v-model="v$.input.name.$model" v-bind="props" compact input-class="tw-p-0"/>
              </template>
            </InputField>
            <InputField
              src="images/category.png" :label="$t('field.type')" :vuelidate="v$.input.type"
            >
              <template #default="props">
                <TextField :model-value="$constant.types()[item.type]['name']"
                           v-bind="props" compact input-class="tw-p-0" readonly/>
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
import {useDialogEditor} from "src/composables/useDialogEditor";
import {required} from '@vuelidate/validators'

import InputField from "components/General/Other/InputField.vue";
import Button from "components/General/Other/Button.vue";
import DisplayField from "components/General/Other/DisplayField.vue";
import TextField from "components/General/Other/TextField.vue";
import UploadMage from "components/General/Other/UploadMage.vue";
import RichTextField from "components/General/Other/RichTextField.vue";
import {useKeyEnforcer} from "src/composables/useKeyEnforcer";

export default {
  name: 'GroupEditor',
  components: {RichTextField, UploadMage, TextField, InputField, DisplayField, Button},
  props: {
    // Editor mode: 'create', 'update', 'copy'
    mode: String,
    // Target item
    item: {
      type: Object,
      default: () => ({
        id: null,
        name: "",
        type: null
      })
    }
  },

  emits: [
    // REQUIRED; need to specify some events that your
    // component will emit through useDialogPluginComponent()
    ...useDialogPluginComponent.emits
  ],

  setup(props) {
    // Use key enforcer
    useKeyEnforcer("group")
    // Make a reactive input
    const input = reactive({...props.item})

    return {
      // Data
      input,
      ...useDialogEditor(input, props.mode),
    }
  },

  validations() {
    return {
      input: {
        id: {},
        name: {required},
        type: {required},
      }
    }
  },
}
</script>
