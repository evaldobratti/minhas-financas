<template>
  <form @submit.prevent="submit">
    <v-text-field ref="nome" v-model="categoriaEdit.nome" label="Nome"></v-text-field>
    <v-btn type="submit">salvar</v-btn>
  </form>
</template>

<script>
import {d} from '../../store/categorias';

export default {
  props: {
    categoria: {
      type: Object,
      default() {
        return { nome: '' }
      }
    }
  },
  data() {
    return {
      categoriaEdit: null
    }
  },
  created() {
    this.categoriaEdit = Object.assign({}, this.categoria);
  },
  methods: {
    showing() {
      this.$nextTick(this.$refs.nome.focus);
    },
    reset() {
    },
    submit () {
      this.$store.dispatch(d.SAVE_CATEGORIA, this.categoriaEdit).then(() => {
        this.reset();
        this.$emit('cadastrado');
      }).catch(() => {});
    }
  }

}
</script>

<style>

</style>
