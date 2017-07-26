<template>
  <form @submit.prevent="submit"style="min-width: 600px">
    <v-layout row>
      <v-flex xs8>
        <v-text-field v-model="nome" label="Nome"></v-text-field>
      </v-flex>
      <v-flex sx4>
        <v-btn type="submit">salvar</v-btn>
      </v-flex>
    </v-layout>
        
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
    submit() {
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
