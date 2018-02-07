package com.example.dglozano.meetapp.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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

import com.example.dglozano.meetapp.R;
import com.example.dglozano.meetapp.actividades.TareaForm;
import com.example.dglozano.meetapp.adapters.TareaItemAdapter;
import com.example.dglozano.meetapp.dao.Dao;
import com.example.dglozano.meetapp.dao.MockDaoEvento;
import com.example.dglozano.meetapp.dao.MockDaoTarea;
import com.example.dglozano.meetapp.modelo.Evento;
import com.example.dglozano.meetapp.modelo.Tarea;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TareasPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TareasPageFragment extends android.support.v4.app.Fragment {

    private final int CREAR_TAREA = 1;
    private final int EDITAR_TAREA = 2;

    private List<Tarea> tareasListDisplayed = new ArrayList<>();
    private TareaItemAdapter mTareaAdapter;
    private RecyclerView mTareasRecyclerview;

    private static final String EVENTO_ID = "EVENTO_ID";

    private Dao<Tarea> dao;
    private Evento evento;
    private List<Tarea> tareasListDelEvento;

    public TareasPageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment TareasPageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TareasPageFragment newInstance(int eventoId) {
        TareasPageFragment fragment = new TareasPageFragment();
        Bundle args = new Bundle();
        args.putInt(EVENTO_ID, eventoId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //TODO cambiar a sqlite cuando se implemente
        evento = MockDaoEvento.getInstance().getById(getArguments().getInt(EVENTO_ID));
        dao = MockDaoTarea.getInstance();
        System.out.println(evento.toString());
        tareasListDelEvento = dao.getAll();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tareas_page, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTareasRecyclerview = view.findViewById(R.id.recvw_tareas_list);
        mTareaAdapter = new TareaItemAdapter(tareasListDisplayed);
        //TODO: ver que mostrar si no hay tareas aun
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(
                getActivity().getApplicationContext());
        mTareasRecyclerview.setLayoutManager(mLayoutManager);
        mTareasRecyclerview.setItemAnimator(new DefaultItemAnimator());
        mTareasRecyclerview.addItemDecoration(new DividerItemDecoration(
                getActivity().getApplicationContext(),
                LinearLayoutManager.VERTICAL));
        mTareasRecyclerview.setAdapter(mTareaAdapter);
        tareasListDisplayed.addAll(tareasListDelEvento);
        mTareaAdapter.notifyDataSetChanged();

        FloatingActionButton fab = view.findViewById(R.id.fab_btn_crear_tarea);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TareaForm.class);
                startActivityForResult(i, CREAR_TAREA);
            }
        });
    }

    private void search(String query) {
        List<Tarea> result = new ArrayList<>();
        for(Tarea t : tareasListDelEvento) {
            if(t.matches(query)) {
                result.add(t);
            }
        }
        tareasListDisplayed.clear();
        tareasListDisplayed.addAll(result);
        System.out.println(tareasListDisplayed);
        mTareaAdapter.notifyDataSetChanged();
    }

    private void restoreOriginalTareasList() {
        tareasListDisplayed.clear();
        tareasListDisplayed.addAll(tareasListDelEvento);
        mTareaAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.toolbar_search);
        final SearchView searchView =
                (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new MyOnQueryTextListener());
        searchView.setOnCloseListener(new MyOnCloseListener());
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
            if(query.trim().isEmpty()) {
                restoreOriginalTareasList();
            }
            return false;
        }
    }

    private class MyOnCloseListener implements SearchView.OnCloseListener {
        @Override
        public boolean onClose() {
            restoreOriginalTareasList();
            return false;
        }
    }

    // FIXME cuando se haga el menú contextual y se seleccione editar, usar este método
    private void editarTarea(Tarea tarea) {
        Intent i = new Intent(getActivity(), TareaForm.class);
        i.putExtra(TareaForm.ID_KEY, tarea.getId());
        startActivityForResult(i, EDITAR_TAREA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case CREAR_TAREA: {
                if(resultCode == RESULT_OK) {
                    // TODO agregar toast
                    tareasListDelEvento = dao.getAll();
                    restoreOriginalTareasList();
                }
                break;
            }
            case EDITAR_TAREA: {
                if(resultCode == RESULT_OK) {
                    // TODO agregar toast
                    tareasListDelEvento = dao.getAll();
                    restoreOriginalTareasList();
                }
                break;
            }
        }
    }
}