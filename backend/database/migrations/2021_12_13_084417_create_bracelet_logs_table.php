<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateBraceletLogsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('bracelet_logs', function (Blueprint $table) {
            $table->id();
            $table->bigInteger('bracelet_id')->unsigned();
            $table->string('action');
            $table->dateTime('date_time');
            $table->timestamps();

            $table->foreign('bracelet_id')->references('id')->on('bracelets')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('bracelet_logs');
    }
}
