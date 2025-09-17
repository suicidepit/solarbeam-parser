

package com.skkrypto.solar_beam.parser.service;

import com.skkrypto.solar_beam.parser.dto.ClmmCreatePoolDto;

import java.util.List;
import java.util.Objects;

/**
 * Raydium CLMM create_pool 전용 파서
 * 입력: Accounts 배열(각 항목은 base58 공개키 문자열)
 *
 * 매핑 규칙 (0-based index):
 *  - accounts[3] -> pool_address = pool_state
 *  - accounts[4] -> mint_a
 *  - accounts[5] -> mint_b
 *  - accounts[6] -> reserve_a
 *  - accounts[7] -> reserve_b
 */
public class RaydiumClmmCreatePoolParser {

    // 인덱스 상수 (/// 스펙 반영)
    private static final int IDX_POOL_STATE = 3;
    private static final int IDX_MINT_A     = 4;
    private static final int IDX_MINT_B     = 5;
    private static final int IDX_RESERVE_A  = 6;
    private static final int IDX_RESERVE_B  = 7;

    /**
     * Accounts 배열에서 필요한 주소 5개를 뽑아 DTO로 반환
     */
    public static ClmmCreatePoolDto parse(List<String> accounts) {
        Objects.requireNonNull(accounts, "accounts must not be null");

        // 최소 길이 체크: 0..7 까지 접근하므로 8개 이상 필요
        if (accounts.size() <= IDX_RESERVE_B) {
            throw new IllegalArgumentException(
                "accounts length must be >= 8 (need indices 3..7), actual=" + accounts.size()
            );
        }

        ClmmCreatePoolDto dto = new ClmmCreatePoolDto();
        dto.setPoolAddress(accounts.get(IDX_POOL_STATE));
        dto.setMintA(accounts.get(IDX_MINT_A));
        dto.setMintB(accounts.get(IDX_MINT_B));
        dto.setReserveA(accounts.get(IDX_RESERVE_A));
        dto.setReserveB(accounts.get(IDX_RESERVE_B));

        // (선택) 간단 검증: Solana pubkey는 보통 32바이트 base58 → 길이 최대 44
        validateVarchar44(dto.getPoolAddress(), "pool_address");
        validateVarchar44(dto.getMintA(), "mint_a");
        validateVarchar44(dto.getMintB(), "mint_b");
        validateVarchar44(dto.getReserveA(), "reserve_a");
        validateVarchar44(dto.getReserveB(), "reserve_b");

        return dto;
    }

    // /// varchar(44) 스펙에 맞춰 대충 sanity check (원하면 Base58 알파벳 검증 추가 가능)
    private static void validateVarchar44(String v, String field) {
        if (v == null || v.isEmpty() || v.length() > 44) {
            throw new IllegalArgumentException(
                "invalid " + field + " (must be 1..44 chars), value='" + v + "'"
            );
        }
    }

    // 간단 데모
    public static void main(String[] args) {
        List<String> accounts = List.of(
            "payer111111111111111111111111111111111111111", // [0] 예시
            "system1111111111111111111111111111111111111", // [1]
            "rent111111111111111111111111111111111111111", // [2]
            "POOLSTATE1111111111111111111111111111111111", // [3] pool_address
            "MINTA11111111111111111111111111111111111111", // [4] mint_a
            "MINTB11111111111111111111111111111111111111", // [5] mint_b
            "RESERVEA11111111111111111111111111111111111", // [6] reserve_a
            "RESERVEB11111111111111111111111111111111111"  // [7] reserve_b
        );

        ClmmCreatePoolDto dto = parse(accounts);
        System.out.println(dto);
    }
}
