package com.retigalo.dglozano.meetapp.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.retigalo.dglozano.meetapp.R;
import com.retigalo.dglozano.meetapp.actividades.ContactosActivity;
import com.retigalo.dglozano.meetapp.adapters.ParticipanteItemAdapter;
import com.retigalo.dglozano.meetapp.dao.DaoEvento;
import com.retigalo.dglozano.meetapp.dao.DaoEventoMember;
import com.retigalo.dglozano.meetapp.dao.SQLiteDaoEvento;
import com.retigalo.dglozano.meetapp.dao.SQLiteDaoPago;
import com.retigalo.dglozano.meetapp.dao.SQLiteDaoParticipante;
import com.retigalo.dglozano.meetapp.modelo.Evento;
import com.retigalo.dglozano.meetapp.modelo.Pago;
import com.retigalo.dglozano.meetapp.modelo.Participante;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ParticipantesPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ParticipantesPageFragment extends android.support.v4.app.Fragment
        implements DialogDeletePagos.NoticeDialogListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private List<Participante> participantesListDisplayed = new ArrayList<>();
    private ParticipanteItemAdapter mParticipanteAdapter;
    private final int CREAR_PARTICIPANTE = 1;

    private DaoEvento daoEvento;
    private SQLiteDaoParticipante daoParticipante;
    private DaoEventoMember<Pago> daoPagos;
    private List<Participante> participantesListDelEvento;
    private LinearLayout mLayoutEmptyMsg;

    private static final String EVENTO_ID = "EVENTO_ID";
    private int eventoId;

    public ParticipantesPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment ParticipantesPageFragment.
     */
    public static ParticipantesPageFragment newInstance(int eventoId) {
        ParticipantesPageFragment fragment = new ParticipantesPageFragment();
        Bundle args = new Bundle();
        args.putInt(EVENTO_ID, eventoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        daoParticipante = new SQLiteDaoParticipante(getActivity());
        daoEvento = new SQLiteDaoEvento(getActivity());
        daoPagos = new SQLiteDaoPago(getActivity());

        eventoId = getArguments().getInt(EVENTO_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_participantes_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        participantesListDelEvento = daoParticipante.getAllDelEventoMenosSinAsignar(eventoId);
        mLayoutEmptyMsg = view.findViewById(R.id.empty_msg_layout_participantes);
        mLayoutEmptyMsg.setVisibility(View.INVISIBLE);
        RecyclerView mParticipantesRecyclerView = view.findViewById(R.id.recvw_participantes_list);
        mParticipanteAdapter = new ParticipanteItemAdapter(participantesListDisplayed);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        mParticipantesRecyclerView.setLayoutManager(mLayoutManager);
        mParticipantesRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mParticipantesRecyclerView.addItemDecoration(new DividerItemDecoration(
                getActivity().getApplicationContext(),
                LinearLayoutManager.VERTICAL));
        mParticipantesRecyclerView.setAdapter(mParticipanteAdapter);
        participantesListDisplayed.clear();
        participantesListDisplayed.addAll(participantesListDelEvento);
        mParticipanteAdapter.notifyDataSetChanged();
        if (participantesListDelEvento.isEmpty()) {
            mLayoutEmptyMsg.setVisibility(View.VISIBLE);
        }
        FloatingActionButton fab = view.findViewById(R.id.fab_btn_agregar_participante);
        fab.setOnClickListener(new MyFabIconOnClickListener(this));
    }

    private void pedirPermisoContactos() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                    builder.setTitle(R.string.permisos_contactos_dialog_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage(R.string.permisos_contactos_dialog_msg);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.READ_CONTACTS},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                Intent i = new Intent(getActivity(), ContactosActivity.class);
                i.putExtra(ContactosActivity.KEY_EVENTO_ID, eventoId);
                startActivityForResult(i, CREAR_PARTICIPANTE);
            }
        } else {
            Intent i = new Intent(getActivity(), ContactosActivity.class);
            i.putExtra(ContactosActivity.KEY_EVENTO_ID, eventoId);
            startActivityForResult(i, CREAR_PARTICIPANTE);
        }
    }

    private void search(String query) {
        List<Participante> result = new ArrayList<>();
        for (Participante p : participantesListDelEvento) {
            if (p.matches(query)) {
                result.add(p);
            }
        }
        participantesListDisplayed.clear();
        participantesListDisplayed.addAll(result);
        mParticipanteAdapter.notifyDataSetChanged();
    }

    private void restoreOriginalParticipantesList() {
        participantesListDisplayed.clear();
        participantesListDisplayed.addAll(participantesListDelEvento);
        mParticipanteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        final SearchView searchView =
                (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new ParticipantesPageFragment.MyOnQueryTextListener());
        searchView.setOnCloseListener(new ParticipantesPageFragment.MyOnCloseListener());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Evento evento = daoEvento.getById(eventoId);
        Integer pos = item.getItemId();
        if (pos == 7) {
            Participante participante = participantesListDisplayed.get(item.getGroupId());
            // El dialogo llama a los metodos onDialogPositiveClick o onDialogNeativeClick
            // con el id del elemento del context menu clickeado.
            if (evento.isDivisionGastosYaHecha()) {
                DialogFragment df = DialogDeletePagos.newInstance(pos, participante.getId());
                df.setTargetFragment(this, 1);
                df.show(getFragmentManager(), "tag");
            } else {
                accionesContextMenu(participante);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int idAccion, int idParticipante) {
        Participante participante = daoParticipante.getById(idParticipante);
        Evento evento = daoEvento.getById(eventoId);
        for (Pago p : daoPagos.getAllDelEvento(eventoId)) {
            daoPagos.delete(p);
        }
        evento.setGastosPorParticipante(0.0);
        evento.setGastosTotales(0.0);
        evento.setDivisionGastosYaHecha(false);
        daoEvento.update(evento);
        if (idAccion == -2) {
            pedirPermisoContactos();
        } else {
            accionesContextMenu(participante);
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog, int idAccion, int idParticipante) {
        switch (idAccion) {
            case -2:
                Toast.makeText(this.getContext(), R.string.participante_no_creado, Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this.getContext(), R.string.participante_no_borrado, Toast.LENGTH_SHORT).show();
        }
    }

    private void accionesContextMenu(Participante participante) {
        Toast.makeText(this.getContext(), R.string.participante_borrado, Toast.LENGTH_SHORT).show();
        daoParticipante.delete(participante);
        participantesListDelEvento = daoParticipante.getAllDelEventoMenosSinAsignar(eventoId);
        restoreOriginalParticipantesList();
        if (participantesListDelEvento.isEmpty()) {
            mLayoutEmptyMsg.setVisibility(View.VISIBLE);
        }
    }

    private class MyOnQueryTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            search(query);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String query) {
            search(query);
            if (query.trim().isEmpty()) {
                restoreOriginalParticipantesList();
            }
            return false;
        }
    }

    private class MyOnCloseListener implements SearchView.OnCloseListener {
        @Override
        public boolean onClose() {
            restoreOriginalParticipantesList();
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CREAR_PARTICIPANTE: {
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this.getContext(), R.string.participante_creado, Toast.LENGTH_SHORT).show();
                    participantesListDelEvento = daoParticipante.getAllDelEventoMenosSinAsignar(eventoId);
                    for (Participante p : participantesListDelEvento) {
                        if (p.esSinAsignar()) {
                            participantesListDelEvento.remove(p);
                        }
                    }
                    mLayoutEmptyMsg.setVisibility(View.INVISIBLE);
                    restoreOriginalParticipantesList();
                }
                break;
            }
        }
    }

    private class MyFabIconOnClickListener implements View.OnClickListener {

        android.support.v4.app.Fragment fragment;

        public MyFabIconOnClickListener(android.support.v4.app.Fragment f) {
            this.fragment = f;
        }

        @Override
        public void onClick(View view) {
            Evento evento = daoEvento.getById(eventoId);
            if (evento.isDivisionGastosYaHecha()) {
                DialogFragment df = DialogDeletePagos.newInstance(-2, -2);
                df.setTargetFragment(fragment, 1);
                df.show(getFragmentManager(), "tag");
            } else {
                pedirPermisoContactos();
            }
        }
    }
}
