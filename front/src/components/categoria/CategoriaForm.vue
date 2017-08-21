<template>
  <form @submit.prevent="submit">
    <v-text-field v-model="nome" label="Nome"></v-text-field>
    <v-btn type="submit">salvar</v-btn>
  </form>
</template>

<script>
import {d} from '../../store/categorias';

export default {
  props: ['categoriaPai'],
  data() {
    return {
      nome: ''
    }
  },
  methods: {
    submit () {
      this.$store.dispatch(d.SAVE_CATEGORIA, {
        pai: this.categoriaPai ? this.categoriaPai.id : null,
        nome: this.nome
      }).then(() => this.nome = '');
    }
  }

}
</script>

<style>

</style>
