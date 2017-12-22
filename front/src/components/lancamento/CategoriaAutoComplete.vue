<template>
  <v-select
    ref="autoComplete"
    :items="categoriasFlat"
    :value="value"
    item-text="nome"
    item-value="id"
    v-bind="$attrs"
    v-on="$listeners"
    @input="$emit('input', $event)"
    autocomplete>
    <template slot="item" slot-scope="data">
      <span :style="{'padding-left': 25 * categoriaDistanciaAteRaiz(data.item) + 'px'}">
          <CategoriaIcone :categoria="data.item"></CategoriaIcone>
          {{ data.item.nome }}
      </span>
    </template>
  </v-select>
</template>

<script>
import CategoriaIcone from '../categoria/CategoriaIcone'
export default {
  props: ['value'],
  methods: {
    categoriaDistanciaAteRaiz(categoria) {
      let pai = categoria.pai;
      let nivel = 0;
      while (pai != null) {
        nivel += 1;
        pai = pai.pai;
      }
      return nivel;
    },
    categoriaAsFlat(categoria) {
      let flat = [ categoria ];
      for (let c of categoria.filhas) {
        for (let f of this.categoriaAsFlat(c))
          flat.push(f);
      }
      return flat;
    },
    focus() {
      
      this.$refs.autoComplete.focus();  
      this.$refs.autoComplete.$refs.input.focus();
    }
  },
  computed: {
    categoriasFlat() {
      return this.$store.state.categorias.list;
    },
    categorias() {
      return this.$store.state.categorias.list;
    },
  },
  components: {
    CategoriaIcone
  }
}
</script>

<style>

</style>
