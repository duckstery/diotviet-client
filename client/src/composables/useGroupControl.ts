import {Dialog} from "quasar";
import {useI18n} from 'vue-i18n';
import {axios, util, constant, notify} from "src/boot";
import GroupEditor from "components/Manage/Group/GroupEditor.vue";
import {useRouteKey} from "src/composables/useRouteKey";
import {Ref} from "vue";

// *************************************************
// Typed
// *************************************************

export type UseGroupControlResources = {
  onGroupControl(mode: string, item: any): void
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup Group control
 *
 * @param {Ref<UnWrapRef<...>>} groupRef
 * @return {object}
 */
export function useGroupControl(groupRef: Ref<any[]>): UseGroupControlResources {
  // Get key
  const key = useRouteKey()
  // i18n
  const $t = useI18n().t

  // On any operation success
  const onSuccessOperation = () => {
    axios.get(`/group/index/${constant.typeByKey(key)['id']}`)
      .then(res => groupRef.value = res.data.payload)
  }

  /**
   * On direct request (non-interactive)
   *
   * @param {string} mode
   * @param {any} item
   */
  const onDirectRequest = (mode: string, item: any) => {
    // Send request
    axios.delete(`/group/delete`, {params: {id: item, type: constant.typeByKey(key)['id']}})
      .then(res => {
        notify($t('message.success', {attr: $t('field.operation')}))
        onSuccessOperation()
      })
      .catch(() => notify($t('message.fail', {attr: $t('field.operation')}), 'negative'))
  }

  /**
   * On interactive request like add, edit, copy
   *
   * @param mode
   * @param item
   */
  const onInteractiveRequest = (mode: string, item: any) => {
    // Preprocess item
    const newItem = {...item, type: constant.typeByKey(key)['id']}
    // Invoke dialog
    Dialog.create({
      component: GroupEditor,
      componentProps: {mode, item: newItem}
    }).onOk(onSuccessOperation)
      .onCancel(() => {

      })
      .onDismiss(() => {

      })
  }

  return {
    /**
     * On group request
     *
     * @param mode
     * @param item
     */
    onGroupControl: (mode, item = null) => {
      if (['create', 'update'].includes(mode)) {
        // Need to be interacted before sending request
        onInteractiveRequest(mode, item)
      } else if (['delete'].includes(mode)) {
        // Directly send to server
        util.promptConfirm($t('message.delete_item')).onOk(() => onDirectRequest(mode, item))
      }
    }
  }
}
