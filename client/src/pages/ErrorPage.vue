<template>
  <q-layout>
    <q-page-container>
      <q-page class="bg-grey-3 tw-p-5 tw-pr-20 tw-flex tw-h-screen">
        <q-card class="bg-blue tw-w-2/3 tw-m-auto" flat>
          <q-card-section class="text-white text-center">
            <div style="font-size: 20vh">
              {{ status }}
            </div>

            <div class="text-h2" style="opacity:.5">
              {{ message }}
            </div>

            <q-btn
              to="/"
              no-caps
              unelevated
              color="white"
              label="Go Back"
              text-color="blue"
              class="tw-mt-10 tw-mb-5"
              icon="fa-solid fa-arrow-left-long"
              @click="$router.go(-1)"
            />
          </q-card-section>
        </q-card>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script>
export default {
  name: 'ErrorPage',

  computed: {
    // Error status code
    status() {
      return this.$route.params.status ?? 404
    },
    // Message status
    message() {
      return this.$t(`error.status_${this.status}`)
    }
  },
}
</script>
