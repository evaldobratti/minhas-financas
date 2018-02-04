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
import { SNACKS } from '../../store/snacks';

export default {
  props: ['lancamento'],
  data() {
    return {
      idNovaConta: null
    }
  },
  created() {
    this.idNovaConta = this.lancamento.idConta;
  },
  methods: {
    submit() {
      this.$store.dispatch(lancamentos.d.TROCA_CONTA, {
        lancamento: this.lancamento, 
        idNovaConta: this.idNovaConta
      }).then(msg => {
        this.$store.commit(SNACKS.m.UPDATE_SUCESSO, msg);
        this.$emit('cadastrado');
      }).catch(err => {
        this.$store.commit(SNACKS.m.UPDATE_ERRO, err);
      });
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
