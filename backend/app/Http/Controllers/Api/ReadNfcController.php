<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Storage;
use Illuminate\Validation\Rule;

class ReadNfcController extends Controller
{
    public function read(Request $request)
    {
        //[{"id":"1","last_action":"out"},{"id":"2","last_action":"out"},{"id":"3","last_action":"out"}]
        $this->validate($request, [
            "action" =>  ["required", Rule::in(["in", "out"])],
            "id"     =>  ["required", "string"]
        ]);

        $bracelets = json_decode(Storage::get('bracelet.json'), true);

        $bracelet = false;
        foreach ($bracelets as $key => $b){

            if($b["uid"] === $request->id){
                $bracelet = $b;
                unset($bracelets[$key]);
            }
        }

        if(!$bracelet){
            return response()->json(["error" => true, "message" => "bracelet not exists"], 400);
        }
        if($bracelet["last_action"] === "disabled"){
            return response()->json(["error" => true, "message" => "The bracelet is deactivated"], 400);
        }
        if($request->action === 'in' && $bracelet["last_action"] === 'in'){
            return response()->json(["error" => true, "message" => "the user is already inside"], 400);
        }
        if($request->action === 'out' && $bracelet["last_action"] === 'out'){
            return response()->json(["error" => true, "message" => "the user has already left"], 400);
        }

        $bracelet["last_action"] = $request->action;
        $bracelet["actions"][] = [
            'action' => $request->action,
            'date'   => Carbon::now()->format('Y-m-d H:i:s')
        ];

        array_push($bracelets, $bracelet);

        Storage::put('bracelet.json', json_encode($bracelets, JSON_PRETTY_PRINT));

        return response()->json(["error" => false, "message" => "Succesfully"], 200);
    }
}
