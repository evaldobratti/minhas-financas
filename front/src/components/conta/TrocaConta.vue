<template>
  <form @submit.prevent="submit">
    <v-select
      label="Conta"
      v-bind:items="contas"
      v-model="idNovaConta"
      item-text="nome"
      item-value="id"
      max-height="auto">
    </v-select>
    <v-btn type="submit">Salvar</v-btn>
  </form>
</template>

<script>
import Vue from 'vue';
import { lancamentos } from '../../store/lancamento';

export default {
  props: ['lancamento'],
  data() {
    return {
      idNovaConta: null
    }
  },
  created() {
    console.info(this.lancamento);
    this.idNovaConta = this.lancamento.idConta;
  },
  methods: {
    submit() {
      this.$store.dispatch(lancamentos.d.TROCA_CONTA, {
        lancamento: this.lancamento, 
        idNovaConta: this.idNovaConta
      });
      this.$emit('cadastrado');
    }
  },
  computed: {
    contas() {
      return this.$store.getters.getContas;
    }
  }

}
</script>

<style>

</style>
