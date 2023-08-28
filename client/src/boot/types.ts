import {Constant} from "boot/constant";
import {Env} from "boot/env";
import {Notifier} from "boot/notify";
import {Loader} from "boot/loader";
import {Util} from "boot/util";
import {LocalAxios} from "boot/axios";
import {Auth} from "boot/auth";
import {ErrorHandler} from "boot/error";

// @ts-ignore
declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    /**
     * Axios
     */
    $axios: LocalAxios
    /**
     * API (Same as Axios)
     */
    $api: LocalAxios
    /**
     * Authentication
     */
    $auth: Auth;
    /**
     * Constant
     */
    $constant: Constant
    /**
     * Environment
     */
    $env: Env
    /**
     * Error handler
     */
    $error: ErrorHandler
    /**
     * Loader
     */
    $loading: Loader
    /**
     * Notifier
     */
    $notify: Notifier
    /**
     * Notifier (warning)
     *
     * @param content
     */
    $notifyWarn: (content: string) => void
    /**
     * Notifier (error)
     *
     * @param content
     */
    $notifyErr: (content: string) => void
    /**
     * Utilities
     */
    $util: Util
  }
}
