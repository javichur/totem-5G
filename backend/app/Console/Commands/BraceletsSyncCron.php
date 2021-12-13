<?php

namespace App\Console\Commands;

use App\Bracelet;
use App\BraceletLog;
use Carbon\Carbon;
use Illuminate\Console\Command;
use Illuminate\Support\Facades\Storage;

class BraceletsSyncCron extends Command
{
    /**
     * The name and signature of the console command.
     *
     * @var string
     */
    protected $signature = 'sync:logs';

    /**
     * The console command description.
     *
     * @var string
     */
    protected $description = 'Sync bracelet logs';

    /**
     * Create a new command instance.
     *
     * @return void
     */
    public function __construct()
    {
        parent::__construct();
    }

    /**
     * Execute the console command.
     *
     * @return int
     */
    public function handle()
    {
        //sync logs
        $this->syncLogs();

        //add or remove bracelets
        $this->syncBracelets();
    }

    public function syncLogs()
    {
        $bracelets_in_cache = json_decode(Storage::get('bracelet.json'), true);


        foreach($bracelets_in_cache as $key => $b){
            $braceletInDb = Bracelet::where('uid', $b["uid"])
                ->with(['logs' => function ($query) {
                    $query->orderBy('date_time', 'desc')->first();
                }])->first();

            if($braceletInDb){

                if(isset($b["actions"]) && is_array($b["actions"])){

                    foreach ($b["actions"] as $a){

                        $last_log = $braceletInDb->logs->first();

                        $format_date = Carbon::createFromFormat('Y-m-d H:i:s', $a["date"]);

                        if( ($last_log && $format_date->greaterThan($last_log->date_time)) || !$last_log){
                            BraceletLog::create([
                                'action'      => $a["action"],
                                'date_time'   => $a["date"],
                                'bracelet_id' => $braceletInDb->id,
                            ]);

                            if($a == end($b["actions"])){
                                $braceletInDb->update(["last_action" => $a["action"]]);
                            }
                        }

                    }

                }
            }
        }

    }

    public function syncBracelets()
    {
        $bracelets_in_db = Bracelet::all();
        $bracelets_uids  = $bracelets_in_db->pluck('uid')->toArray();

        $bracelets_in_cache = json_decode(Storage::get('bracelet.json'), true);

        //Delete bracelets from cache
        $bracelets_in_cache_array_ids = [];
        foreach ($bracelets_in_cache as $key => $b) {
            if(!in_array($b["uid"], $bracelets_uids)){
                unset($bracelets_in_cache[$key]);
            }
            $bracelets_in_cache_array_ids[] = $b["uid"];
        }

        //Add new bracelets to cache
        foreach ($bracelets_in_db->whereNotIn('uid', $bracelets_in_cache_array_ids) as $b){
            array_push($bracelets_in_cache, [
                "uid"         => $b->uid,
                "last_action" => $b->last_action
            ]);
        }



        if(count($bracelets_uids) > 0){
            foreach ($bracelets_in_cache as $key => $b) {
                $filter = $bracelets_in_db->first(function ($value, $key) use ($b){
                    return $value["uid"] == $b["uid"];
                });

                if($filter){
                    $bracelets_in_cache[$key]["last_action"] = $filter->last_action;
                }
            }
        }

        Storage::put('bracelet.json', json_encode($bracelets_in_cache, JSON_PRETTY_PRINT));
    }
}
