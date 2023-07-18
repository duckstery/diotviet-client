import {date, Dialog, exportFile} from "quasar";
import {axios, util, notify, error} from "src/boot";
import {useI18n} from 'vue-i18n';
import {useRouteKey} from "src/composables/useRouteKey";

/**
 * Setup request mechanism for pages of type Manage
 *
 * @param {any} invoker
 * @param {function} customizer
 * @param {function} fetchCb
 * @return {object}
 */
export function usePageRequest(invoker, customizer, fetchCb) {
  // Get key
  const key = useRouteKey()
  // i18n
  const $t = useI18n().t

  /**
   * On send file request
   *
   * @param {string} mode
   * @param {File} item
   */
  const onImportRequest = (mode, item) => {
    // Send request
    axios.post(
      (mode === 'legacy' ? 'legacy' : '') + `/${key}/import`,
      util.craftFormData({file: item}),
      {headers: {"Content-Type": "multipart/form-data"}}
    )
      .then(onSuccessOperation)
      .catch(error.$422.bind(this, 'input'))
  }

  /**
   * On export file request
   */
  const onExportRequest = () => {
    // Send request
    axios.get(`/${key}/export`, {responseType: "blob"})
      .then(res => exportFile(`${key}_${date.formatDate(Date.now(), 'YYMMDD')}.csv`, res.data))
  }

  /**
   * On direct request (non-interactive)
   *
   * @param {string} mode
   * @param {array} item
   */
  const onDirectRequest = (mode, item) => {
    let params = item

    if (mode === 'delete') {
      params = {params: {ids: item.ids}}
    }

    // Send request
    axios[mode](`/${key}/${mode}`, params)
      .then(onSuccessOperation)
      .catch(error.switch({
        410: fetchCb,
        default: (err) => notify($t('message.fail', {attr: $t('field.operation')}), 'negative', err)
      }))
  }

  /**
   * On interactive request like add, edit, copy
   *
   * @param mode
   * @param item
   * @param customizer
   */
  const onInteractiveRequest = (mode, item) => {
    // Create props so item("null") won't override Editor default value
    const componentProps = {mode, ...(util.isUnset(customizer) ? [] : customizer())}

    // Add item
    if (item !== null) {
      componentProps.item = {
        ...item,
        id: mode === 'copy' ? null : item.id,
        code: mode === 'copy' ? null : item.code,
        category: item.categoryId,
        groups: item.groupIds
      }
    }

    // Invoke dialog
    return typeof invoker === 'function'
      ? invoker()
      : Dialog.create({
        component: invoker,
        componentProps: componentProps
      }).onOk(fetchCb)
        .onCancel(() => {

        })
        .onDismiss(() => {

        })
  }

  // On any operation success
  const onSuccessOperation = () => {
    notify($t('message.success', {attr: $t('field.operation')}))
    fetchCb()
  }

  return {
    /**
     * On request an operation
     *
     * @param mode
     * @param item
     */
    onRequest: (mode, item = null) => {
      if (['create', 'update', 'copy'].includes(mode)) {
        // Need to be interacted before sending request
        onInteractiveRequest(mode, item)
      } else if (['legacy', 'import'].includes(mode)) {
        // Need to send file to server
        util.promptConfirm().onOk(() => onImportRequest(mode, item))
      } else if (['export'].includes(mode)) {
        // Need to send file to server
        util.promptConfirm().onOk(() => onExportRequest())
      } else if (['delete', 'patch'].includes(mode)) {
        // Directly send to server
        util.promptConfirm().onOk(() => onDirectRequest(mode, item))
      } else {
        fetchCb()
      }
    }
  }
}
