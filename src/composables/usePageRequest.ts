import {date, Dialog, exportFile} from "quasar";
import {axios, util, notify, error} from "src/boot";
import {useI18n} from 'vue-i18n';
import {useRouteKey} from "src/composables/useRouteKey";
import {Component} from "vue"
import {AxiosError} from "axios";

// *************************************************
// Typed
// *************************************************

export type UsePageRequestResources = {
  onRequest(mode: string, item: string): void
}

// *************************************************
// Implementation
// *************************************************

/**
 * Setup request mechanism for pages of type Manage
 *
 * @param {any} invoker
 * @param {function} customizer
 * @param {function} fetchCb
 * @return {UsePageRequestResources}
 */
export function usePageRequest(invoker: () => any | Component, customizer: () => {}, fetchCb: () => void): UsePageRequestResources {
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
  const onImportRequest = (mode: string, item: any) => {
    // Send request
    axios.post(
      (mode === 'legacy' ? 'legacy' : '') + `/${key}/import`,
      util.craftFormData({file: item}),
      {headers: {"Content-Type": "multipart/form-data"}}
    )
      .then(onSuccessOperation)
      // @ts-ignore
      .catch(error.$422.bind(this, 'input'))
  }

  /**
   * On export file request
   */
  const onExportRequest = () => {
    // Send request
    axios.post(`/${key}/export`, {}, {responseType: "blob"})
      // @ts-ignore
      .then(res => exportFile(`${key}_${date.formatDate(Date.now(), 'YYMMDD')}.csv`, res.data))
      .catch(error.any)
  }

  /**
   * On direct request (non-interactive)
   *
   * @param {string} mode
   * @param {array} item
   */
  const onDirectRequest = (mode: string, item: any) => {
    let params = item

    if (mode === 'delete') {
      params = {params: {ids: item.ids}}
    }

    // Send request
    // @ts-ignore
    axios[mode](`/${key}/${mode}`, params)
      .then(onSuccessOperation)
      .catch(error.switch({
        410: fetchCb,
        default: (err: AxiosError) => notify(
          `${$t('message.fail', {attr: $t('field.operation')})}: ${err.response.data.message}`,
          'negative',
          err
        )
      }))
  }

  /**
   * On interactive request like add, edit, copy
   *
   * @param mode
   * @param item
   */
  const onInteractiveRequest = (mode: string, item: any) => {
    // Create props so item("null") won't override Editor default value
    const componentProps = {mode, ...(util.isUnset(customizer) ? [] : customizer())}

    // Add item
    if (item !== null) {
      // @ts-ignore
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
    onRequest: (mode: string, item: any = null) => {
      if (['create', 'update', 'copy'].includes(mode)) {
        // Need to be interacted before sending request
        onInteractiveRequest(mode, item)
      } else if (['legacy', 'import'].includes(mode)) {
        // Need to send file to server
        util.promptConfirm($t('message.import_file')).onOk(() => onImportRequest(mode, item))
      } else if (['export'].includes(mode)) {
        // Need to send file to server
        util.promptConfirm($t('message.export_data')).onOk(() => onExportRequest())
      } else if (['delete', 'patch'].includes(mode)) {
        // Directly send to server
        util.promptConfirm($t('message.action_on_item', {attr: $t(`field.${mode}`).toLowerCase()})).onOk(() => onDirectRequest(mode, item))
      } else {
        fetchCb()
      }
    }
  }
}
