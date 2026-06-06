import Vue from "vue";
import VueI18n from "vue-i18n";
import zh from "./zh";
import en from "./en";

Vue.use(VueI18n);

const i18n = new VueI18n({
  locale: localStorage.getItem("lang") || "zh",
  fallbackLocale: "zh",
  messages: { zh, en },
  silentTranslationWarn: true,
});

export default i18n;
