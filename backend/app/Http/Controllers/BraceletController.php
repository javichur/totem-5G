<?php

namespace App\Http\Controllers;

use App\Bracelet;
use App\BraceletLog;
use Illuminate\Http\Request;
use Illuminate\Validation\Rule;

class BraceletController extends Controller
{
    public function index(Request $request)
    {
        $bracelets = Bracelet::search($request->search)->orderBy('created_at', 'desc')->get();

        return view('bracelets.index', compact('bracelets'));
    }

    public function create()
    {
        return view('bracelets.create');
    }

    public function store(Request $request)
    {
        $this->validate($request, [
              "name"  => 'required|string',
              "uid"   =>  'required|string|unique:bracelets,uid',
              "state" => ['required', Rule::in(['in', 'out','disabled'])]
        ]);

        Bracelet::create([
            'name'        => $request->name,
            'uid'         => $request->uid,
            'last_action' => $request->state
        ]);

        $request->session()->flash('status', __('Bracelet created successfully'));

        return redirect('/bracelets');
    }

    public function edit(Bracelet $bracelet)
    {
        return view('bracelets.edit', compact('bracelet'));
    }

    public function update(Request $request, Bracelet $bracelet)
    {
        $this->validate($request, [
            "name"  => 'required|string',
            "state" => ['required', Rule::in(['in', 'out', 'disabled'])]
        ]);

        $bracelet->update([
            'name' => $request->name,
            'last_action' => $request->state
        ]);
        $request->session()->flash('status', __('Bracelet updated successfully'));

        return redirect('/bracelets');
    }

    public function delete(Request $request, Bracelet $bracelet)
    {
        $bracelet->delete();

        $request->session()->flash('status', __('Bracelet deleted successfully'));

        return redirect('/bracelets');
    }

    public function logs(Bracelet $bracelet)
    {
        $logs = $bracelet->logs()->orderBy('created_at', 'desc')->get();

        return view('bracelets.logs', compact('logs'));
    }

    public function allLogs()
    {
        $logs = BraceletLog::with('bracelet')->orderBy('date_time', 'desc')->get();

        return view('bracelets.general_logs', compact('logs'));
    }

    public function status(Request $request)
    {
        if (! $request->hasValidSignature()) {
            abort(403);
        }

        $bracelet = Bracelet::findOrFail($request->bracelet);

        return view('bracelets.activate_or_deactivate', compact('bracelet'));
    }

    public function activate(Request $request)
    {
        if (! $request->hasValidSignature()) {
            abort(403);
        }

        $bracelet = Bracelet::findOrFail($request->bracelet);

        if($bracelet->last_action != 'disabled'){
            $request->session()->flash('status-error', __('Your bracelet is already active'));
        }else{
            $bracelet->update(['last_action' => 'in']);
            $request->session()->flash('status', __('Bracelet activated correctly'));
        }

        return view('bracelets.activate_or_deactivate', compact('bracelet'));
    }
}
