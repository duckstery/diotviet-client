import {Constant} from "boot/constant";
import {Env} from "boot/env";
import {Notifier} from "boot/notify";
import {Loader} from "boot/loader";
import {Util} from "boot/util";
import {LocalAxios} from "boot/axios";
import {Auth} from "boot/auth";

// @ts-ignore
declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $axios: LocalAxios
    $api: LocalAxios
    $auth: Auth;
    $constant: Constant
    $env: Env
    $loading: Loader
    $notify: Notifier
    $notifyWarn: (content: string) => void
    $notifyErr: (content: string) => void
    $util: Util
  }
}
