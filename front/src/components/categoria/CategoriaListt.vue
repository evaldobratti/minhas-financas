<template>
  <div>
    <div @mouseover="hovered" @mouseleave="unhovered" class="categoria">
      <v-icon @click="abre" v-if="categoria.filhas.length === 0">insert_drive_file</v-icon>
      <v-icon @click="abre" v-else-if="!aberto">folder</v-icon>
      <v-icon @click="abre" v-else>folder_open</v-icon>
      <span @click="abre">{{ categoria.nome }}</span>
      <transition name="fade">
      <v-btn v-if="showAdd" @click.native.stop="showModal" primary fab small dark class="small-fab-btn">
        <v-icon>add</v-icon>
      </v-btn>
      </transition>
      <v-dialog v-model="dialog" lazy absolute>
        <v-layout row justify-center>
          <v-card>
            <v-card-title>
              <div class="headline">Nova sub-categoia de {{ categoria.nome }}</div>
            </v-card-title>
            <v-card-text>
              <CategoriaForm :categoriaPai="categoria"></CategoriaForm>
            </v-card-text>
          </v-card>
        </v-layout>
      </v-dialog>
    </div>
    <div v-if="aberto" style="margin-left: 25px">
      <CategoriaListt v-for="f in categoria.filhas" :key="f.id" :categoria="f"></CategoriaListt>
    </div>
  </div>
</template>

<script>
import CategoriaForm from './CategoriaForm';

export default {
  props: ['categoria'],
  name: 'CategoriaListt',
  data() {
    return {
      dialog: false,
      aberto: true,
      showAdd: false
    }
  },
  methods: {
    abre() {
      this.aberto = !this.aberto;
    },
    hovered(e) {
      e.stopPropagation();
      this.showAdd = true;
    },
    unhovered() {
      this.showAdd = false;
    },
    showModal() {
      this.dialog = true;
    }
  },
  components: {
    CategoriaForm
  }

}
</script>

<style>
.categoria {
  height: 30px;
}  

.fade-enter-active, .fade-leave-active {
  transition: opacity .2s
}
.fade-enter, .fade-leave-to {
  opacity: 0
}
</style>
