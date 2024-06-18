# FLUTTER + cardinalcommerce

Este proyecto es de ejemplo en integración entre flutter y cardinalcommerce

primero estoy haciendo la vinculacion entre flutter y briantree, espero estar en lo correcto
la libreria de briantree esta usando el sdk de cardinalcommerce,
el objetivo es ir reemplazando en lo posibe a briantree

## Getting Started

crear un archivo .env con TOKENIZATION_KEY
esto lo traes cuando te crees una cuenta en briantree

aparte debes tener un backend para confirmar el pago
para esto también debes de tener la variable en .env HOST

el backend se encargara de hacer el pago, la app de hacer la tokenización

en flutter-braintree/android/build.grandle
debes de agregar

```sh
        maven {
            url "https://cardinalcommerceprod.jfrog....."
            credentials {
                username 'braintree_team_sdk'
                password 'sadsadasdsadsa'
            }
        }
```
