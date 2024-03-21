import {app, BrowserWindow, ipcMain} from 'electron';
import {resolve, join} from 'path';
import {platform} from 'os';
import {autoUpdater} from "electron-updater";

// needed in case process is undefined under Linux
const currentPlatform = process.platform || platform();

let mainWindow: BrowserWindow | undefined;

function createWindow() {
  /**
   * Initial window options
   */
  mainWindow = new BrowserWindow({
    icon: resolve(__dirname, 'icons/icon.png'), // tray icon
    title: 'Diotviet',
    width: 1000,
    height: 600,
    autoHideMenuBar: true,
    useContentSize: true,
    webPreferences: {
      contextIsolation: true,
      // More info: https://v2.quasar.dev/quasar-cli-vite/developing-electron-apps/electron-preload-script
      preload: resolve(__dirname, process.env.QUASAR_ELECTRON_PRELOAD),
    },
  });

  mainWindow.loadURL(process.env.APP_URL);

  if (process.env.DEBUGGING) {
    // if on DEV or Production with debug enabled
    mainWindow.webContents.openDevTools();
  } else {
    // we're on production; no access to devtools pls
    mainWindow.webContents.on('devtools-opened', () => {
      // mainWindow?.webContents.closeDevTools();
      mainWindow?.webContents.openDevTools();
    });
  }

  mainWindow.on('closed', () => {
    mainWindow = undefined;
  });

  mainWindow.maximize()

  return mainWindow
}

// Detect multiple instance
if (!app.requestSingleInstanceLock()) app.quit()

app.on('second-instance', () => {
  if (mainWindow) mainWindow.isMinimized() ? mainWindow.restore() : mainWindow.focus()
})

app.whenReady().then(() => {
  // Set autoDownload = false
  autoUpdater.autoDownload = false
  // Check for updates
  ipcMain.handle('check-for-updates', async () => {
    // On update-available
    autoUpdater.on('update-available', () => {
      mainWindow.webContents.send('update-available')
    })

    await autoUpdater.checkForUpdates()
  })

  // Updates
  ipcMain.on('updates', async () => {
    // On update-downloaded, quit and install
    autoUpdater.on('update-downloaded', async () => autoUpdater.quitAndInstall())
    // Download updates
    await autoUpdater.downloadUpdate()
  })

  const mainWindow = createWindow()
});

app.on('window-all-closed', () => {
  if (currentPlatform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', () => {
  if (mainWindow === undefined) {
    createWindow();
  }
});
