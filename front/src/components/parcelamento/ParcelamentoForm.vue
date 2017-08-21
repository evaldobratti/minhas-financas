<template>
  <form @submit.prevent="submit" style="min-width: 600px">
    <v-layout row>
      <v-flex xs2>
        <v-text-field 
          label="Esta é a parcela"
          v-model="inicioParcelas"></v-text-field>  
      </v-flex>
      <v-flex xs5>
        <v-text-field 
          label="de"
          v-model="quantidadeParcelas"></v-text-field >  
          parcelas
      </v-flex>
    </v-layout>
    <v-layout row>
      <v-flex xs5>
        <v-btn type="submit">Salvar</v-btn>
      </v-flex>
    </v-layout>
  </form>
</template>

<script>
import mapGetSet from '../../store/mapGetSet';
import { PARCELAMENTOS } from '../../store/parcelamentos';
export default {
  methods: {
    submit() {
      this.$store.dispatch(PARCELAMENTOS.d.SUBMIT_FORM);
    }
  },
  computed: {
    ...mapGetSet({
      inicioParcelas: ['parcelamentos.form.inicioParcelas', PARCELAMENTOS.m.UPDATE_FORM_INICIO_PARCELAS],
      quantidadeParcelas: ['parcelamentos.form.quantidadeParcelas', PARCELAMENTOS.m.UPDATE_FORM_QUANTIDADE_PARCELAS],
    })
  }
  
}
</script>

<style>
  
</style>
