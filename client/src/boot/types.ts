import {Constant} from "boot/constant";
import {Env} from "boot/env";
import {Notifier} from "boot/notify";
import {Loader} from "boot/loader";
import {Util} from "boot/util";
import {LocalAxios} from "boot/axios";
import {Auth} from "boot/auth";
import {ErrorHandler} from "boot/error";
import {ConfigType, Dayjs} from "dayjs";
import {RouteLocationNormalizedLoaded, Router} from "vue-router";

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
    $auth: Auth
    /**
     * Constant
     */
    $constant: Constant
    /**
     * Dayjs instance
     */
    $dayjs: (date?: ConfigType) => Dayjs
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
    /**
     * Current route
     */
    $route: RouteLocationNormalizedLoaded
    /**
     * Vue router
     */
    $router: Router
  }
}
