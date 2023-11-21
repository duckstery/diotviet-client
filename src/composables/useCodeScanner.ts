import {BarcodeScanner} from '@capacitor-mlkit/barcode-scanning';
import {notify} from "src/boot";
import {useI18n} from "vue-i18n";
import {Dialog, DialogChainObject} from "quasar";
// @ts-ignore
import CodeScanner from "components/Work/CodeScanner.vue";

// *************************************************
// Typed
// *************************************************

export type UseCodeScannerResources = {
  scanCode: () => Promise<DialogChainObject>
}

// *************************************************
// Implementation
// *************************************************

export function useCodeScanner(): UseCodeScannerResources {
  const $t = useI18n().t

  return {
    scanCode: async () => {
      // Check if not supported
      if (!await isSupported()) notify($t('message.not_supported'), 'negative')
      // Check if app does not have permission
      if (!await hasPermissions()) notify($t('message.permission_denied'), 'negative')

      return Dialog.create({component: CodeScanner})
    }
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
