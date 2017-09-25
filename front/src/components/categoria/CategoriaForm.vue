<template>
  <form @submit.prevent="submit">
    <v-text-field ref="nome" v-model="nome" label="Nome"></v-text-field>
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
    showing() {
      this.$nextTick(this.$refs.nome.focus);
    },
    reset() {
      this.nome = '';
    },
    submit () {
      this.$store.dispatch(d.SAVE_CATEGORIA, {
        pai: this.categoriaPai ? this.categoriaPai.id : null,
        nome: this.nome
      }).then(() => {
        this.reset();
        this.$emit('cadastrado');
      }).catch(() => {});
    }
  }

}
</script>

<style>

</style>
