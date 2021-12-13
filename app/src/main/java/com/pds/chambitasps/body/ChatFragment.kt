package com.pds.chambitasps.body

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pds.chambitasps.R
import com.pds.chambitasps.body.chat_utils.AdapterChat
import com.pds.chambitasps.body.chat_utils.ChatModel
import kotlinx.android.synthetic.main.fragment_chat.view.*
import java.util.*


class ChatFragment : Fragment() {

    private var idService = ""
    private lateinit var auth: FirebaseAuth
    var db = Firebase.firestore

    private lateinit var messageAdapterChat: AdapterChat

    private lateinit var root: View
    private lateinit var registration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            idService = bundle.getString("idService")!!
        }
        auth = Firebase.auth

        messageAdapterChat = AdapterChat(requireContext())

        registration = db.collection("chat")
            .document(idService)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("CHAT", "Error al recibir mensajes", error)
                    return@addSnapshotListener
                }
                if (value != null) {
                    for (documentChange: DocumentChange in value.documentChanges) {
                        if (documentChange.type == DocumentChange.Type.ADDED) {
                            Log.d("CHAT", "Mensaje de ${documentChange.document.data["from"]}")
                            Log.d("CHAT", "Mensaje de ${documentChange.document.data["type"]}")
                            Log.d("CHAT", "Mensaje de ${documentChange.document.data["message"]}")
                            Log.d("CHAT", "Mensaje de ${documentChange.document.data["timestamp"]}")
                            messageAdapterChat.addMessage(
                                ChatModel(
                                    documentChange.document.data["from"].toString(),
                                    documentChange.document.data["message"].toString(),
                                    documentChange.document.data["type"].toString()
                                )
                            )
                        }
                    }
                    messageAdapterChat.notifyDataSetChanged()
                    if (::root.isInitialized) {
                        root.chatList.setSelection(messageAdapterChat.count - 1)
                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_chat, container, false)

        root.chatList.adapter = messageAdapterChat

        root.imageView5.setOnClickListener {

            val message = root.editTextTextPersonName.text.toString()

            val data = hashMapOf(
                "from" to "Pablo Alvarez",
                "type" to "prestador",
                "message" to message,
                "timestamp" to Timestamp(Date())
            )

            db.collection("chat")
                .document(idService)
                .collection("messages")
                .add(data)
                .addOnSuccessListener {
                    Log.d("CHAT", "Mensaje con id ${it.id} agregado")
                }
                .addOnFailureListener {
                    Log.d("CHAT", "El mensaje no pudo ser registrado")
                }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (::registration.isInitialized) registration.remove()
    }

}