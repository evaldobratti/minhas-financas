import Vue from 'vue';

export const events = {
    SIGN_IN: 'signIn',
    SIGN_OUT: 'signOut'
}

export const bus = new Vue();

export default {
    events, bus
}