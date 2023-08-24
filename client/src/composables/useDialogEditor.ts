import {QDialog, useDialogPluginComponent} from 'quasar'
import {useVuelidate} from "@vuelidate/core";
import {axios, util, notify, error} from "src/boot"
import {useI18n} from "vue-i18n";
import {useRouteKey} from "src/composables/useRouteKey";
import {Ref, UnwrapNestedRefs} from "@vue/reactivity";
import {Validation} from "@vuelidate/core";

// *************************************************
// Typed
// *************************************************

export type UseDialogEditorExtension = { v$: Ref<Validation>, onConfirm(): void }
export type UseDialogEditorResources<T> = {
  dialogRef: Ref<QDialog | undefined>,
  onHide(): void,
  onCancel(): void,
  ok(payload?: T): void,
} & UseDialogEditorExtension

// *************************************************
// Implementation
// *************************************************

/**
 * Setup dialog editor
 *
 * @param {object} inputRef
 * @param {string} mode
 * @return {*}
 */
export function useDialogEditor<T>(inputRef: UnwrapNestedRefs<T>, mode = 'create'): UseDialogEditorResources<T> {
  // REQUIRED; must be called inside of setup()
  const {dialogRef, onDialogHide, onDialogOK, onDialogCancel} = useDialogPluginComponent()
  // dialogRef      - Vue ref to be applied to QDialog
  // onDialogHide   - Function to be used as handler for @hide on QDialog
  // onDialogOK     - Function to call to settle dialog with "ok" outcome
  //                    example: onDialogOK() - no payload
  //                    example: onDialogOK({ /*.../* }) - with payload
  // onDialogCancel - Function to call to settle dialog with "cancel" outcome

  // Get $t
  const $t = useI18n().t
  // Extension
  // @ts-ignore
  const extension: UseDialogEditorExtension = {v$: null, vonConfirm: null}
  // Default dialog behavior
  if (!util.isUnset(inputRef)) {
    // Get key
    const key = useRouteKey()

    // Validator (Vuelidator)
    extension.v$ = useVuelidate({$rewardEarly: true})

    /**
     * On confirm
     *
     * @return {Promise<void>}
     */
    extension.onConfirm = async () => {
      if (util.isUnset(extension.v$.value.$validate)) {
        return
      }

      // Validate file
      if (await extension.v$.value.$validate()) {

        // Craft formData
        // @ts-ignore
        const formData = util.craftFormData(inputRef)
        // Send request
        axios.post(`/${key}/store`, formData, {headers: {"Content-Type": "multipart/form-data"}})
          .then(res => {
            notify($t("message.success", {attr: $t(`field.${mode}`)}))
            // Close dialog
            onDialogOK(res.data.payload)
          })
          .catch(error.switch({
            410: onDialogOK,
            422: [{v$: extension.v$.value}, 'input'],
            default: error.any
          }))
      } else {
        console.warn(extension.v$.value)
        // Notify about invalid
        notify($t("message.invalid_input"), 'negative')
      }
    }
  }

  return {
    // This is REQUIRED;
    // Need to inject these (from useDialogPluginComponent() call)
    // into the vue scope for the vue html template
    dialogRef,
    onHide: onDialogHide,
    onCancel: onDialogCancel,
    ok: onDialogOK,

    // Extension
    ...extension
  }
}
