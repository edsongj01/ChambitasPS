package com.pds.chambitasps.util

class Constants {

    companion object {

        const val LOCATION_SERVICE_ID = 175

        const val ACTION_START_LOCATION_SERVICE = "startLocationService"

        const val ACTION_STOP_LOCATION_SERVICE = "stopLocationService"

        private const val PACKAGE_NAME = "com.google.android.gms.location.chambitasps.ForegroundLocationService"

        const val EXTRA_LOCATION = "$PACKAGE_NAME.location"

        const val ACTION_BROADCAST = "$PACKAGE_NAME.broadcast"

        // Estados del servicio

        const val SERVICE_EN_ESPERA = "En espera"
        const val SERVICE_PENDIENTE = "Pendiente"
        const val SERVICE_EN_CAMINO = "En camino"
        const val SERVICE_TERMINADO = "Terminado"
        const val SERVICE_FINALIZADO = "Finalizado"

        const val SERVICE_CANCELADO = "Cancelado"

        const val NOTIFICATION_REQUEST = "request"
        const val NOTIFICATION_MESSAGE = "message"

    }

}