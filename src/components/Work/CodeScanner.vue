<template>
  <q-dialog ref="dialogRef" maximized class="tw-visible tw-bg-transparent" seamless
            @show="startScan" @before-hide="stopScan" @hide="onDialogHide">
    <q-card class="q-dialog-plugin tw-bg-transparent">
      <q-bar class="tw-h-[50px] bg-primary text-white">
        {{ $t('field.scanning') }}
        <q-space/>
        <Button color="white" flat icon="fa-solid fa-close" @click="onDialogCancel"/>
      </q-bar>
      <q-card-section :style="`height: ${$q.screen.height - 50}px`">
        <div class="square" ref="square"/>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

<script>
import Button from "components/General/Other/Button.vue";

import {useDialogPluginComponent} from 'quasar'
import {BarcodeFormat, BarcodeScanner} from "@capacitor-mlkit/barcode-scanning";
import {templateRef} from "@vueuse/core";
import {onMounted, onUnmounted} from "vue";

export default {
  name: 'CodeScanner',
  components: {Button},

  emits: [...useDialogPluginComponent.emits],

  setup() {
    const {dialogRef, onDialogHide, onDialogOK, onDialogCancel} = useDialogPluginComponent()

    // Get scanning zone ref
    const square = templateRef('square')
    // Adjust scanning zone size
    const getCornerPoints = () => {
      // Get currentRect
      const currentRect = square.value.getBoundingClientRect()
      // Get scaledRect
      const scaledRect = {
        left: currentRect.left * window.devicePixelRatio,
        right: currentRect.right * window.devicePixelRatio,
        top: currentRect.top * window.devicePixelRatio,
        bottom: currentRect.bottom * window.devicePixelRatio,
        width: currentRect.width * window.devicePixelRatio,
        height: currentRect.height * window.devicePixelRatio,
      }
      // Calculate detection corner pointsx
      return [
        // Top left
        [scaledRect.left, scaledRect.top],
        // Top right
        [scaledRect.left + scaledRect.width, scaledRect.top],
        // Bottom right
        [scaledRect.left + scaledRect.width, scaledRect.top + scaledRect.height],
        // Bottom left
        [scaledRect.left, scaledRect.top + scaledRect.height],
      ]
    }

    // Start scanning process
    const startScan = async () => {
      // Get corner points
      const detectPoints = getCornerPoints()

      // // Add the `barcodeScanned` listener
      const listener = await BarcodeScanner.addListener('barcodeScanned', async event => {
        const cornerPoints = event.barcode.cornerPoints;
        if (
          detectPoints[0][0] > cornerPoints[0][0] ||
          detectPoints[0][1] > cornerPoints[0][1] ||
          detectPoints[1][0] < cornerPoints[1][0] ||
          detectPoints[1][1] > cornerPoints[1][1] ||
          detectPoints[2][0] < cornerPoints[2][0] ||
          detectPoints[2][1] < cornerPoints[2][1] ||
          detectPoints[3][0] > cornerPoints[3][0] ||
          detectPoints[3][1] < cornerPoints[3][1]
        ) {
          return;
        }
        // Remove listener
        listener.remove()
        // Send data back to client
        onDialogOK(event.barcode)
      });

      // Start the barcode scanner
      await BarcodeScanner.startScan({
        formats: [BarcodeFormat.QrCode, BarcodeFormat.Code128]
      });
    };

    // Stop scanning process
    const stopScan = async () => {
      // Make all elements in the WebView visible again
      document.querySelector('body')?.classList.remove('tw-invisible');
      // Stop the barcode scanner
      await BarcodeScanner.stopScan();
    };

    onMounted(() => {
      // The camera is visible behind the WebView, so that you can customize the UI in the WebView.
      // However, this means that you have to hide all elements that should not be visible.
      // You can find an example in our demo repository.
      // In this case we set a class `barcode-scanner-active`, which then contains certain CSS rules for our app.
      document.querySelector('body')?.classList.add('tw-invisible');

      // Handle back button
      document.addEventListener('backbutton', onDialogCancel);
    })

    onUnmounted(() => document.removeEventListener('backbutton', onDialogCancel))

    return {
      dialogRef, onDialogHide, onDialogOK, onDialogCancel,
      startScan: startScan, stopScan: stopScan
    }
  },
}
</script>


<style scoped lang="scss">
.square {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  border-radius: 16px;
  width: 250px;
  height: 250px;
  border: 3px solid white;
  box-shadow: 0 0 0 4000px rgba(0, 0, 0, 0.3);
}
</style>