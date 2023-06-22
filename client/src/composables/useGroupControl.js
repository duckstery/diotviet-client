import {Dialog} from "quasar";
import {useI18n} from 'vue-i18n';
import {axios, util, notify} from "src/boot";
import GroupEditor from "components/Manage/Group/GroupEditor.vue";
import {useRouteKey} from "src/composables/useRouteKey";

/**
 * Setup Group control
 *
 * @param {Ref<UnWrapRef<...>>} groupRef
 * @return {object}
 */
export function useGroupControl(groupRef) {
  // Get key
  const key = useRouteKey()
  // i18n
  const $t = useI18n().t

  // On any operation success
  const onSuccessOperation = () => {
    axios.get(`/group/index/${util.getPredefinedTypesByKey(key)['id']}`)
      .then(res => groupRef.value = res.data.payload)
  }

  /**
   * On direct request (non-interactive)
   *
   * @param {string} mode
   * @param {array} item
   */
  const onDirectRequest = (mode, item) => {
    // Send request
    axios.delete(`/group/delete`, {params: {id: item, type: util.getPredefinedTypesByKey(key)['id']}})
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
  const onInteractiveRequest = (mode, item) => {
    // Preprocess item
    const newItem = {...item, type: util.getPredefinedTypesByKey(key)['id']}
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
        util.promptConfirm().onOk(() => onDirectRequest(mode, item))
      }
    }
  }
}
