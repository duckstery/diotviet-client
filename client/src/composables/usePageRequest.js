import {axios} from "boot/axios";
import {util} from "boot/util";
import {Dialog} from "quasar";
import {useI18n} from 'vue-i18n';
import {saveAs} from 'file-saver';
import {date} from "quasar";
import {notify} from "boot/notify";
import {error} from "boot/error";

/**
 * Setup page request
 *
 * @param {string} key
 * @param {any} dialogComponent
 * @param {function} dialogCustomizer
 * @param {function} onSuccess
 * @return {object}
 */
export function usePageRequest(key, dialogComponent, dialogCustomizer, onSuccess) {
  // i18n
  const $t = useI18n().t

  /**
   * On send file request
   *
   * @param {string} mode
   * @param {File} item
   */
  const onImportRequest = (mode, item) => {
    // Craft formData
    const formData = util.craftFormData({file: item})

    // Send request
    axios.post((mode === 'legacy' ? 'legacy' : '') + `/${key}/import`, formData, {headers: {"Content-Type": "multipart/form-data"}})
      .then(onSuccessOperation)
      .catch(error.$422.bind(this, 'input'))
  }

  /**
   * On export file request
   */
  const onExportRequest = () => {
    // Send request
    axios.get(`/${key}/export`, {responseType: "blob"})
      .then(res => saveAs(res.data, `${key}_${date.formatDate(Date.now(), 'YYMMDD')}.csv`))
  }

  /**
   * On direct request (non-interactive)
   *
   * @param {string} mode
   * @param {array} item
   */
  const onDirectRequest = (mode, item) => {
    let api, params

    if (mode === 'delete') {
      api = 'delete'
      params = {params: {ids: item}}
    } else {
      // Split mode by _
      const [option, target] = mode.split('_')
      // Convert option to boolean
      api = 'patch'
      params = {ids: item, target: target, option: option === 'start'}
    }

    // Send request
    axios[api](`/${key}/${api}`, params)
      .then(onSuccessOperation)
      .catch(() => notify($t('message.fail', {attr: $t('field.operation')}), 'warning'))
  }

  /**
   * On interactive request like add, edit, copy
   *
   * @param {string} mode
   * @param {*} item
   */
  const onInteractiveRequest = (mode, item) => {
    // Create props so item("null") won't override Editor default value
    const componentProps = {mode, ...dialogCustomizer()}

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
    Dialog.create({
      component: dialogComponent,
      componentProps: componentProps
    }).onOk(onSuccess)
      .onCancel(() => {

      })
      .onDismiss(() => {

      })
  }

  // On any operation success
  const onSuccessOperation = () => {
    notify($t('message.success', {attr: $t('field.operation')}))
    onSuccess()
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
      } else if (['delete', 'path'].includes(mode)) {
        // Directly send to server
        util.promptConfirm().onOk(() => onDirectRequest(mode, item))
      } else {
        onSuccess()
      }
    }
  }
}
