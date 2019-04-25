/*!
 *  Adobe Summit EMEA 2019 - TL30 : Building a PWA with AEM
 *
 * @description :
 *                  This description must be updated
 *
 * @file : /apps/aem-pwa-blog/clientlibs/clientlib-exercises/scripts/ex05_background-sync.js
 *
 *
 *
 * @project Adobe Summit EMEA - TL30
 * @date 2019-05-15
 * @author Olympe KASSA, Adobe <kassa@adobe.com>
 * @licensor Adobe
 * @site none
 *
 */
;
(function (window, navigator, document) {
    'use strict';

    /**
     * ---------------------------------------------
     *
     *          EXERCISE 05
     *
     * ---------------------------------------------
     *
     *
     * @type {{init: AdobeSummit.Exercise05.init}}
     */
    window.AdobeSummit.Exercise05 = {


        init:function (channel) {

            var canvasElement = $('#canvas');
            var titleInput = $('#title').val();
            var tagsInput = $('#tags').val();
            var $postButton = $('#post-btn');

            /**
             *  When the service worker has synced the post with
             *  AEM, it will then receive via a channel the response
             *  from the server.
             */
            if(channel){
                channel.addEventListener('message', function (event) {
                    if(event.data.type == "synced-post"){
                        //alert("The service worker has synchronized the post")
                        var $backSync = $("#background-sync");
                        $backSync.modal("show");
                    }

                } );
            }


            /**
             *  When the user clicks on the "send post" button,
             *  the post will be register into the sync manager
             *  for a synchronization later on.
             */
            if($postButton) {
                $postButton.click(function() {

                    if (titleInput.trim() === '' || tagsInput.trim() === '') {
                        alert('Please enter valid data!');
                        return;
                    }

                    window.AdobeSummit.closeCreatePostModal();

                    if ('serviceWorker' in navigator && 'SyncManager' in window) {
                        /**
                         ======================================================

                         Exercise 04 : Background synchronization
                         -----------
                         Copy the code from this file : /apps/aem-pwa-blog/config.exercise-04/ex05-code-to-paste-01.txt
                         below this commented block  :

                         ======================================================
                         **/
                    }
                });
            }
        }


    }

}(window, navigator, document));