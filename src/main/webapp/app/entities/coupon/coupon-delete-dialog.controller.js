(function() {
    'use strict';

    angular
        .module('salesOrderApp')
        .controller('CouponDeleteController',CouponDeleteController);

    CouponDeleteController.$inject = ['$uibModalInstance', 'entity', 'Coupon'];

    function CouponDeleteController($uibModalInstance, entity, Coupon) {
        var vm = this;

        vm.coupon = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Coupon.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
