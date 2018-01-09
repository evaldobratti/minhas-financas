<template>
  <form @submit.prevent="submit">
    <v-text-field 
    type="number"
      label="Esta é a parcela"
      v-model="inicioParcelas"></v-text-field>  
    <v-text-field 
      type="number"
      label="de"
      v-model="quantidadeParcelas"></v-text-field >  
    <v-layout justify-space-around>
      <v-btn type="submit">Salvar</v-btn>
    </v-layout>
  </form>
</template>

<script>
import mapGetSet from '../../store/mapGetSet';
import { PARCELAMENTOS } from '../../store/parcelamentos';
export default {
  props: ['lancamento'],
  watch: {
    lancamento(lancamento) {
      this.lancamentoInicial = this.lancamento;
    }
  },
  methods: {
    submit() {
      this.$store.dispatch(PARCELAMENTOS.d.SUBMIT_FORM).then(() => {
        this.$emit('cadastrado');
      });
    }
  }
  
}
</script>

<style>
  
</style>
