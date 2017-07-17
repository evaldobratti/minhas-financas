<template>
  <v-expansion-panel-content>
    <div slot="header">{{categoria.nome}}</div>
    <v-card>
      <v-card-text>
        <CategoriaForm :categoriaPai="categoria"></CategoriaForm>
        <v-expansion-panel expand>
          <CategoriaList v-for="f in categoria.filhas" :key="f.id" :categoria="f"></CategoriaList>
        </v-expansion-panel>
      </v-card-text>
    </v-card>
  </v-expansion-panel-content>
</template>

<script>
import CategoriaForm from './CategoriaForm';

export default {
  props: ['categoria'],
  name: 'CategoriaList',
  components: {
    CategoriaForm
  }
}
</script>

<style>
  
</style>
