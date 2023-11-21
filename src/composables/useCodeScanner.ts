import {BarcodeScanner, Barcode} from '@capacitor-mlkit/barcode-scanning';
import {notify} from "src/boot";
import {useI18n} from "vue-i18n";
import {Dialog} from "quasar";
// @ts-ignore
import CodeScanner from "components/Work/CodeScanner.vue";


// *************************************************
// Implementation
// *************************************************

/**
 * Setup code scanner
 *
 * @param onSuccess
 */
export function useCodeScanner(onSuccess: (code: Barcode) => void): () => Promise<void> {
  const $t = useI18n().t

  return async () => {
    // Check if not supported
    if (!await isSupported()) notify($t('message.not_supported'), 'negative')
    // Check if app does not have permission
    if (!await hasPermissions()) notify($t('message.permission_denied'), 'negative')

    Dialog.create({component: CodeScanner}).onOk(onSuccess)
  }
}

/**
 * Check if supported
 */
const isSupported = async () => {
  const {supported} = await BarcodeScanner.isSupported();
  return supported;
};

/**
 * Check if having permissions
 */
const hasPermissions = async () => {
  // Request and get permissions
  let {camera} = await BarcodeScanner.requestPermissions();
  // Check if not granted
  if (camera !== 'granted' && camera !== 'limited') {
    // Open setting to change permission
    await BarcodeScanner.openSettings();
    // Recheck permissions
    camera = (await BarcodeScanner.checkPermissions()).camera;
  }

  return camera === 'granted' || camera === 'limited';
};
