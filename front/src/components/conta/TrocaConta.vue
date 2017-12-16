<template>
  <form @submit.prevent="submit">
    <v-select
      label="Conta"
      v-bind:items="contas"
      v-model="conta"
      item-text="nome"
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
  methods: {
    submit() {
      this.$store.dispatch(lancamentos.d.LANCAMENTO_EDICAO_SUBMIT, this.lancamento);
      this.$emit('cadastrado');
    }
  },
  computed: {
    contas() {
      return this.$store.getters.getContas;
    },
    conta: {
      get() {
        const contaId = this.lancamento ? this.lancamento.conta.id : null;
        return this.contas.find(c => c.id == contaId);
      },
      set(val) {
        if (this.lancamento)
          this.lancamento.idConta = val.id;
      }
    }
  }

}
</script>

<style>

</style>
